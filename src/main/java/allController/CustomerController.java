package allController;

import allModels.Customer;
import dbUtils.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class CustomerController {

    @FXML
    private TextField nameField, phoneField, locationField;
    @FXML
    private TextArea addressField;
    @FXML
    private ComboBox<String> referenceComboBox;
    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, Integer> colId;
    @FXML
    private TableColumn<Customer, String> colName, colPhone, colLocation, colReference;
    @FXML
    private Pagination pagination;

    private final Connection conn = Database.getInstance().getConnection();
    private final ObservableList<Customer> masterList = FXCollections.observableArrayList();
    private static final int ROWS_PER_PAGE = 10;

    private Customer selectedCustomer = null;

    @FXML
    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colReference.setCellValueFactory(new PropertyValueFactory<>("referenceBy"));

        loadReferenceEmployees();
        loadCustomers();

        customerTable.getSelectionModel().selectedItemProperty().addListener((obs, old, c) -> {
            selectedCustomer = c;
            if (c != null) {
                fillForm(c);
            }
        });

        pagination.setPageFactory(this::createPage);
    }

    /* ================= LOAD ================= */

    private void loadReferenceEmployees() {

        ObservableList<String> list = FXCollections.observableArrayList("None");

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT id, full_name FROM employee WHERE is_deleted=0")) {

            while (rs.next()) {
                list.add(rs.getInt("id") + " - " + rs.getString("full_name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        referenceComboBox.setItems(list);
        referenceComboBox.setValue("None");
    }

    private void loadCustomers() {

        masterList.clear();

        String sql = "SELECT c.*, e.full_name AS ref_name FROM customer c LEFT JOIN employee e ON c.reference_by = e.id WHERE c.is_deleted=0 ORDER BY c.id DESC";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                masterList.add(new Customer(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("location"),
                        rs.getString("ref_name") == null ? "None" : rs.getString("ref_name")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        updatePagination();
    }

    /* ================= VALIDATION ================= */

    private boolean validate() {

        if (nameField.getText().trim().isEmpty()) {
            showAlert("Full Name is required");
            return false;
        }

        if (!phoneField.getText().trim().isEmpty()
                && !phoneField.getText().matches("\\d{10}")) {
            showAlert("Phone must be 10 digits");
            return false;
        }

        return true;
    }

    /* ================= INSERT ================= */

    @FXML
    private void insertCustomer() {

        if (!validate()) return;

        try {

            Integer refId = getReferenceId();

            String sql = "INSERT INTO customer (full_name, phone, address, location, reference_by) VALUES (?,?,?,?,?)";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, nameField.getText());
            ps.setString(2, phoneField.getText());
            ps.setString(3, addressField.getText());
            ps.setString(4, locationField.getText());

            if (refId == null)
                ps.setNull(5, Types.INTEGER);
            else
                ps.setInt(5, refId);

            ps.executeUpdate();

            clearForm();
            loadCustomers();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ================= UPDATE ================= */

    @FXML
    private void updateCustomer() {

        if (selectedCustomer == null) {
            showAlert("Please select a customer to update.");
            return;
        }

        if (!validate()) return;

        try {

            Integer refId = getReferenceId();

            String sql = "UPDATE customer SET full_name=?, phone=?, address=?, location=?, reference_by=? WHERE id=?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, nameField.getText());
            ps.setString(2, phoneField.getText());
            ps.setString(3, addressField.getText());
            ps.setString(4, locationField.getText());

            if (refId == null)
                ps.setNull(5, Types.INTEGER);
            else
                ps.setInt(5, refId);

            ps.setInt(6, selectedCustomer.getId());

            ps.executeUpdate();

            clearForm();
            loadCustomers();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ================= DELETE (Soft Delete) ================= */

    @FXML
    private void deleteCustomer() {

        if (selectedCustomer == null) {
            showAlert("Please select a customer to delete.");
            return;
        }

        try {

            String sql = "UPDATE customer SET is_deleted=1 WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, selectedCustomer.getId());
            ps.executeUpdate();

            clearForm();
            loadCustomers();

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

    private TableView<Customer> createPage(int pageIndex) {

        int from = pageIndex * ROWS_PER_PAGE;
        int to = Math.min(from + ROWS_PER_PAGE, masterList.size());

        customerTable.setItems(
                FXCollections.observableArrayList(masterList.subList(from, to))
        );

        return customerTable;
    }

    /* ================= UTIL ================= */

    private Integer getReferenceId() {
        String value = referenceComboBox.getValue();

        if (value == null || value.equals("None")) return null;

        // Only parse if it contains a dash
        if (value.contains(" - ")) {
            try {
                return Integer.parseInt(value.split(" - ")[0]);
            } catch (NumberFormatException e) {
                return null; // fallback if parsing fails
            }
        }

        return null;
    }

    private void fillForm(Customer c) {

        nameField.setText(c.getFullName());
        phoneField.setText(c.getPhone());
        addressField.setText(c.getAddress());
        locationField.setText(c.getLocation());
        referenceComboBox.setValue(c.getReferenceBy());
    }

    @FXML
    private void clearForm() {

        selectedCustomer = null;

        nameField.clear();
        phoneField.clear();
        addressField.clear();
        locationField.clear();
        referenceComboBox.setValue("None");

        customerTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String msg) {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}