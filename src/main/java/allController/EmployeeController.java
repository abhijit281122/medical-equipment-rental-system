package allController;

import allModels.Employee;
import dbUtils.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class EmployeeController {

    @FXML
    private TextField codeField, nameField, phoneField,
            emailField, salaryField, searchField;
    @FXML
    private TextArea addressField;
    @FXML
    private DatePicker joiningDatePicker;
    @FXML
    private ComboBox<String> roleComboBox, statusComboBox, roleFilterCombo;
    @FXML
    private TableView<Employee> employeeTable;
    @FXML
    private TableColumn<Employee, Integer> colId;
    @FXML
    private TableColumn<Employee, String> colCode, colName,
            colPhone, colRole, colStatus;
    @FXML
    private TableColumn<Employee, Double> colSalary;
    @FXML
    private Pagination pagination;

    private final Connection conn = Database.getInstance().getConnection();
    private static final int ROWS_PER_PAGE = 10;

    private ObservableList<Employee> masterList = FXCollections.observableArrayList();
    private Employee selectedEmployee = null;

    @FXML
    public void initialize() {

        loadRoles();
        statusComboBox.setItems(FXCollections.observableArrayList("Active", "Inactive"));

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("employeeCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("roleName"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        formatSalaryColumn();
        styleStatusColumn();

        loadEmployees();

        employeeTable.getSelectionModel().selectedItemProperty().addListener((obs, old, emp) -> {
            if (emp != null) {
                selectedEmployee = emp;
                fillForm(emp);
            }
        });

        pagination.setPageFactory(this::createPage);
    }

    /* ==============================
       LOAD DATA
    ============================== */

    private void loadRoles() {
        ObservableList<String> roles = FXCollections.observableArrayList("All Roles");

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT role_name FROM employee_role")) {

            while (rs.next()) {
                roles.add(rs.getString("role_name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        roleComboBox.setItems(roles.filtered(r -> !r.equals("All Roles")));
        roleFilterCombo.setItems(roles);
        roleFilterCombo.setValue("All Roles");
    }

    private void loadEmployees() {

        masterList.clear();

        String sql = "SELECT e.*, r.role_name FROM employee e JOIN employee_role r ON e.role_id = r.id WHERE e.is_deleted = 0 ORDER BY e.id DESC";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                masterList.add(new Employee(
                        rs.getInt("id"),
                        rs.getString("employee_code"),
                        rs.getString("full_name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getDate("joining_date") != null ?
                                rs.getDate("joining_date").toLocalDate() : null,
                        rs.getDouble("salary"),
                        rs.getString("role_name"),
                        rs.getString("status")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        updatePagination();
    }

    /* ==============================
       CRUD
    ============================== */

    @FXML
    private void saveEmployee() {

        try {

            String roleQuery = "SELECT id FROM employee_role WHERE role_name=?";
            PreparedStatement roleStmt = conn.prepareStatement(roleQuery);
            roleStmt.setString(1, roleComboBox.getValue());
            ResultSet roleRs = roleStmt.executeQuery();

            if (!roleRs.next()) return;
            int roleId = roleRs.getInt("id");

            if (selectedEmployee == null) {

                String insert = "INSERT INTO employee (employee_code, full_name, phone, email, address, joining_date, salary, role_id, status) VALUES (?,?,?,?,?,?,?,?,?)";

                PreparedStatement ps = conn.prepareStatement(insert);
                ps.setString(1, codeField.getText());
                ps.setString(2, nameField.getText());
                ps.setString(3, phoneField.getText());
                ps.setString(4, emailField.getText());
                ps.setString(5, addressField.getText());
                ps.setDate(6, Date.valueOf(joiningDatePicker.getValue()));
                ps.setDouble(7, Double.parseDouble(salaryField.getText()));
                ps.setInt(8, roleId);
                ps.setString(9, statusComboBox.getValue());
                ps.executeUpdate();

            } else {

                String update = "UPDATE employee SET full_name=?, phone=?, email=?, address=?, joining_date=?, salary=?, role_id=?, status=? WHERE id=?";

                PreparedStatement ps = conn.prepareStatement(update);
                ps.setString(1, nameField.getText());
                ps.setString(2, phoneField.getText());
                ps.setString(3, emailField.getText());
                ps.setString(4, addressField.getText());
                ps.setDate(5, Date.valueOf(joiningDatePicker.getValue()));
                ps.setDouble(6, Double.parseDouble(salaryField.getText()));
                ps.setInt(7, roleId);
                ps.setString(8, statusComboBox.getValue());
                ps.setInt(9, selectedEmployee.getId());
                ps.executeUpdate();
            }

            clearForm();
            loadEmployees();
            pagination.setPageFactory(this::createPage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteEmployee() {

        if (selectedEmployee == null) return;

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE employee SET is_deleted=1 WHERE id=?");
            ps.setInt(1, selectedEmployee.getId());
            ps.executeUpdate();

            clearForm();
            loadEmployees();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ==============================
       PAGINATION
    ============================== */

    private void updatePagination() {

        int pageCount = (int) Math.ceil((double) masterList.size() / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount == 0 ? 1 : pageCount);

        pagination.setPageFactory(this::createPage);
    }

    private TableView<Employee> createPage(int pageIndex) {

        int from = pageIndex * ROWS_PER_PAGE;
        int to = Math.min(from + ROWS_PER_PAGE, masterList.size());

        employeeTable.setItems(FXCollections.observableArrayList(
                masterList.subList(from, to)
        ));

        return employeeTable;
    }

    /* ==============================
       UI ENHANCEMENTS
    ============================== */

    private void formatSalaryColumn() {

        colSalary.setCellFactory(tc -> new TableCell<Employee, Double>() {

            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);

                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(String.format("₹ %,.2f", value));
                }
            }
        });
    }

    private void styleStatusColumn() {

        colStatus.setCellFactory(tc -> new TableCell<Employee, String>() {

            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);

                    if (status.equalsIgnoreCase("Active")) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void fillForm(Employee emp) {
        codeField.setText(emp.getEmployeeCode());
        nameField.setText(emp.getFullName());
        phoneField.setText(emp.getPhone());
        emailField.setText(emp.getEmail());
        addressField.setText(emp.getAddress());
        joiningDatePicker.setValue(emp.getJoiningDate());
        salaryField.setText(String.valueOf(emp.getSalary()));
        roleComboBox.setValue(emp.getRoleName());
        statusComboBox.setValue(emp.getStatus());
    }

    @FXML
    private void clearForm() {
        selectedEmployee = null;
        codeField.clear();
        nameField.clear();
        phoneField.clear();
        emailField.clear();
        addressField.clear();
        salaryField.clear();
        joiningDatePicker.setValue(null);
        roleComboBox.setValue(null);
        statusComboBox.setValue(null);
        employeeTable.getSelectionModel().clearSelection();
    }
}