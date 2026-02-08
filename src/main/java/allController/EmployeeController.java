package allController;

import allDao.EmployeeDAO;
import allModels.Employee;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

public class EmployeeController {


    // ---------- FORM FIELDS ----------
    @FXML
    private TextField codeField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField roleField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField emailField;
    @FXML
    public TextArea addressField;

    // ---------- TABLE ----------
    @FXML
    private TableView<Employee> table;
    @FXML
    private TableColumn<Employee, String> codeCol;
    @FXML
    private TableColumn<Employee, String> nameCol;
    @FXML
    private TableColumn<Employee, String> roleCol;
    @FXML
    private TableColumn<Employee, String> phoneCol;
    @FXML
    private TableColumn<Employee, String> emailCol;
    @FXML
    public TableColumn<Employee, String> addressCol;

    private final String loggedInUser = "admin";
    private boolean updateConfirmed = false;

    // ---------- INIT ----------
    @FXML
    public void initialize() throws SQLException {
        if (codeCol != null) codeCol.setCellValueFactory(new PropertyValueFactory<>("employeeCode"));
        if (nameCol != null) nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        if (roleCol != null) roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        if (phoneCol != null) phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        if (emailCol != null) emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        if (addressCol != null) addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        loadEmployees();
        enableCreateMode(); // 👈 important

        if (table != null && table.getSelectionModel() != null) {
            table.getSelectionModel().selectedItemProperty().addListener(
                    (obs, oldVal, selected) -> populateForm(selected)
            );
        }
    }

    // ---------- LOAD ----------
    private void loadEmployees() throws SQLException {
        if (table != null) {
            table.setItems(EmployeeDAO.getAllActiveEmployees());
        }
    }

    // ---------- POPULATE FORM ----------
    private void populateForm(Employee e) {
        if (e == null) return;

        if (codeField != null) codeField.setText(e.getEmployeeCode());
        if (nameField != null) nameField.setText(e.getName());
        if (roleField != null) roleField.setText(e.getRole());
        if (phoneField != null) phoneField.setText(e.getPhone());
        if (emailField != null) emailField.setText(e.getEmail());
        if (addressField != null) addressField.setText(e.getAddress());

        enableReadOnlyMode();   // 🔒 lock on select
        updateConfirmed = false;
    }

    // ---------- CREATE ----------
    @FXML
    void saveEmployee() {
        try {
            Employee e = new Employee(
                    0,
                    codeField != null ? codeField.getText() : "",
                    nameField != null ? nameField.getText() : "",
                    roleField != null ? roleField.getText() : "",
                    phoneField != null ? phoneField.getText() : "",
                    emailField != null ? emailField.getText() : "",
                    addressField != null ? addressField.getText() : "",
                    true
            );

            EmployeeDAO.saveEmployee(e, loggedInUser);
            showInfo("Employee saved successfully");

            clearForm();
            loadEmployees();

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    // ---------- UPDATE WITH CONFIRM ----------
    @FXML
    void updateEmployee() {
        if (table == null || table.getSelectionModel() == null) {
            showError("Table not initialized properly");
            return;
        }

        Employee selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select an employee first");
            return;
        }

        // Step 1: confirmation
        if (!updateConfirmed) {
            Alert alert = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Do you want to edit this employee?\nFields will be unlocked.",
                    ButtonType.YES, ButtonType.NO
            );

            if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                unlockFormForUpdate();
                updateConfirmed = true;
            }
            return;
        }

        // Step 2: update
        try {
            if (nameField != null) selected.setName(nameField.getText());
            if (roleField != null) selected.setRole(roleField.getText());
            if (phoneField != null) selected.setPhone(phoneField.getText());
            if (emailField != null) selected.setEmail(emailField.getText());
            if (addressField != null) selected.setAddress(addressField.getText());

            EmployeeDAO.updateEmployee(selected, loggedInUser);

            showInfo("Employee updated successfully");
            clearForm();
            loadEmployees();

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    // ---------- DELETE (SOFT) ----------
    @FXML
    void deleteEmployee() throws SQLException {
        if (table == null || table.getSelectionModel() == null) return;

        Employee selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Soft delete this employee?",
                ButtonType.YES, ButtonType.NO
        );

        if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            EmployeeDAO.softDeleteEmployee(selected.getId(), loggedInUser);
            showInfo("Employee deleted");

            clearForm();
            loadEmployees();
        }
    }

    // ---------- CLEAR ----------
    @FXML
    void clearForm() {
        if (codeField != null) codeField.clear();
        if (nameField != null) nameField.clear();
        if (roleField != null) roleField.clear();
        if (phoneField != null) phoneField.clear();
        if (emailField != null) emailField.clear();
        if (addressField != null) addressField.clear();

        if (table != null && table.getSelectionModel() != null) {
            table.getSelectionModel().clearSelection();
        }
        updateConfirmed = false;
        enableCreateMode(); // ✅ VERY IMPORTANT
    }

    // ---------- FORM MODES ----------
    private void enableCreateMode() {
        if (codeField != null) codeField.setDisable(false);
        if (nameField != null) nameField.setDisable(false);
        if (roleField != null) roleField.setDisable(false);
        if (phoneField != null) phoneField.setDisable(false);
        if (emailField != null) emailField.setDisable(false);
        if (addressField != null) addressField.setDisable(false);
    }

    private void enableReadOnlyMode() {
        if (codeField != null) codeField.setDisable(true);
        if (nameField != null) nameField.setDisable(true);
        if (roleField != null) roleField.setDisable(true);
        if (phoneField != null) phoneField.setDisable(true);
        if (emailField != null) emailField.setDisable(true);
        if (addressField != null) addressField.setDisable(true);
    }

    private void unlockFormForUpdate() {
        if (nameField != null) nameField.setDisable(false);
        if (roleField != null) roleField.setDisable(false);
        if (phoneField != null) phoneField.setDisable(false);
        if (emailField != null) emailField.setDisable(false);
        if (addressField != null) addressField.setDisable(false);
        if (codeField != null) codeField.setDisable(true); // 🚫 never editable
    }

    // ---------- ALERTS ----------
    private void showError(String msg) {
        if (msg == null) msg = "Unknown error";
        new Alert(Alert.AlertType.ERROR, msg).show();
    }

    private void showInfo(String msg) {
        if (msg == null) msg = "Operation completed";
        new Alert(Alert.AlertType.INFORMATION, msg).show();
    }
}