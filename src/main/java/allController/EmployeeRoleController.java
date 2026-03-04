package allController;

import allModels.EmployeeRole;
import dbUtils.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class EmployeeRoleController {

    @FXML
    private TextField roleNameField;
    @FXML
    private TableView<EmployeeRole> roleTable;
    @FXML
    private TableColumn<EmployeeRole, Integer> colId;
    @FXML
    private TableColumn<EmployeeRole, String> colRoleName;
    @FXML
    private Pagination pagination;

    private final Connection conn = Database.getInstance().getConnection();
    private static final int ROWS_PER_PAGE = 10;

    private ObservableList<EmployeeRole> masterList = FXCollections.observableArrayList();
    private EmployeeRole selectedRole = null;

    @FXML
    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colRoleName.setCellValueFactory(new PropertyValueFactory<>("roleName"));

        loadRoles();

        roleTable.getSelectionModel().selectedItemProperty().addListener((obs, old, role) -> {
            if (role != null) {
                selectedRole = role;
                roleNameField.setText(role.getRoleName());
            }
        });

        pagination.setPageFactory(this::createPage);
    }

    /* ================= LOAD ================= */

    private void loadRoles() {

        masterList.clear();

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT * FROM employee_role WHERE is_deleted=0 ORDER BY id DESC")) {

            while (rs.next()) {
                masterList.add(new EmployeeRole(
                        rs.getInt("id"),
                        rs.getString("role_name")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        updatePagination();
    }

    /* ================= CRUD ================= */

    @FXML
    private void saveRole() {

        if (roleNameField.getText().isEmpty()) return;

        try {

            if (selectedRole == null) {

                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO employee_role (role_name) VALUES (?)");
                ps.setString(1, roleNameField.getText());
                ps.executeUpdate();

            } else {

                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE employee_role SET role_name=? WHERE id=?");
                ps.setString(1, roleNameField.getText());
                ps.setInt(2, selectedRole.getId());
                ps.executeUpdate();
            }

            clearForm();
            loadRoles();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteRole() {

        if (selectedRole == null) return;

        try {

            // Prevent delete if used in employee
            PreparedStatement check = conn.prepareStatement(
                    "SELECT COUNT(*) FROM employee WHERE role_id=? AND is_deleted=0");
            check.setInt(1, selectedRole.getId());
            ResultSet rs = check.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                showAlert("Cannot delete role. Employees are assigned to it.");
                return;
            }

            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE employee_role SET is_deleted=1 WHERE id=?");
            ps.setInt(1, selectedRole.getId());
            ps.executeUpdate();

            clearForm();
            loadRoles();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ================= PAGINATION ================= */

    private void updatePagination() {
        int pageCount = (int) Math.ceil((double) masterList.size() / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount == 0 ? 1 : pageCount);
        pagination.setPageFactory(this::createPage);
    }

    private TableView<EmployeeRole> createPage(int pageIndex) {

        int from = pageIndex * ROWS_PER_PAGE;
        int to = Math.min(from + ROWS_PER_PAGE, masterList.size());

        roleTable.setItems(FXCollections.observableArrayList(
                masterList.subList(from, to)
        ));

        return roleTable;
    }

    /* ================= UTILS ================= */

    @FXML
    private void clearForm() {
        selectedRole = null;
        roleNameField.clear();
        roleTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
