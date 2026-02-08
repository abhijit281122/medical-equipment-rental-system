package allDao;

import allModels.Employee;
import dbUtils.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class EmployeeDAO {

    /* ================= CREATE ================= */
    public static void saveEmployee(Employee e, String user) throws SQLException {
        String sql = "INSERT INTO employee (employee_code, full_name, role, phone, email, address, created_by) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = Database.getInstance()
                .getConnection().prepareStatement(sql)) {

            ps.setString(1, e.employeeCodeProperty().get());
            ps.setString(2, e.nameProperty().get());
            ps.setString(3, e.roleProperty().get());
            ps.setString(4, e.phoneProperty().get());
            ps.setString(5, e.emailProperty().get());
            ps.setString(6, e.addressProperty().get());
            ps.setString(7, user);
            ps.executeUpdate();
        }
    }

    /* ================= READ ================= */
    public static ObservableList<Employee> getAllActiveEmployees() throws SQLException {
        ObservableList<Employee> list = FXCollections.observableArrayList();

        String sql = "SELECT * FROM employee WHERE is_active = 1";

        Connection con = Database.getInstance().getConnection();
        try (
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)
        ) {
            while (rs.next()) {
                list.add(new Employee(
                        rs.getInt("id"),
                        rs.getString("employee_code"),
                        rs.getString("full_name"),
                        rs.getString("role"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getInt("is_active") == 1
                ));
            }
        }

        return list;
    }



    /* ================= UPDATE ================= */
    public static void updateEmployee(Employee e, String user) throws SQLException {
        String sql = "UPDATE employee SET full_name = ?, role = ?, phone = ?, email = ?, address = ?, updated_at = CURRENT_TIMESTAMP, updated_by = ? WHERE id = ?";

        try (PreparedStatement ps = Database.getInstance()
                .getConnection().prepareStatement(sql)) {

            ps.setString(1, e.nameProperty().get());
            ps.setString(2, e.roleProperty().get());
            ps.setString(3, e.phoneProperty().get());
            ps.setString(4, e.emailProperty().get());
            ps.setString(5, e.addressProperty().get());
            ps.setString(6, user);
            ps.setInt(7, e.getId());
            ps.executeUpdate();
        }
    }

    /* ================= SOFT DELETE ================= */
    public static void softDeleteEmployee(int id, String user) throws SQLException {
        String sql = "UPDATE employee SET is_active = 0, updated_at = CURRENT_TIMESTAMP, updated_by = ? WHERE id = ?";

        try (PreparedStatement ps = Database.getInstance()
                .getConnection().prepareStatement(sql)) {

            ps.setString(1, user);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }
}

