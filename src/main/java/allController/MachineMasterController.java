package allController;

import allModels.Machine;
import dbUtils.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class MachineMasterController {

    @FXML
    private TextField machineCodeField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField categoryField;
    @FXML
    private TextField manufacturerField;
    @FXML
    private TextArea descriptionField;

    @FXML
    private Button saveBtn;
    @FXML
    private Button clearBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button editBtn;

    @FXML
    private TableView<Machine> machineTable;
    @FXML
    private TableColumn<Machine, Integer> colId;
    @FXML
    private TableColumn<Machine, String> colCode;
    @FXML
    private TableColumn<Machine, String> colName;
    @FXML
    private TableColumn<Machine, String> colCategory;
    @FXML
    private TableColumn<Machine, String> colManufacturer;
    @FXML
    private TableColumn<Machine, String> colDescription;

    @FXML
    private Pagination pagination;

    @FXML
    private Label totalUnitsLabel;
    @FXML
    private Label availableUnitsLabel;
    @FXML
    private Label rentedUnitsLabel;
    @FXML
    private Label maintenanceUnitsLabel;

    private final Connection connection = Database.getInstance().getConnection();
    private static final int ROWS_PER_PAGE = 10;

    private boolean isUpdateMode = false;
    private int selectedMachineId = -1;

    @FXML
    public void initialize() {

        // Table mapping
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("machineCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("machineName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colManufacturer.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        loadPagination();

        machineTable.setOnMouseClicked(e -> loadSelectedMachineData());
        editBtn.setOnAction(e -> enableEditMode());

        saveBtn.setOnAction(e -> saveOrUpdateMachine());
        deleteBtn.setOnAction(e -> deleteMachine());
        clearBtn.setOnAction(e -> clearForm());
    }

    // ================= PAGINATION =================
    private void loadPagination() {
        int totalRows = getTotalMachineCount();
        int pageCount = (int) Math.ceil((double) totalRows / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount);

        pagination.setPageFactory(this::createPage);
    }

    private TableView<Machine> createPage(int pageIndex) {
        loadMachines(pageIndex * ROWS_PER_PAGE);
        return machineTable;
    }

    private void loadMachines(int offset) {

        ObservableList<Machine> list = FXCollections.observableArrayList();

        String sql = "SELECT * FROM machine LIMIT ? OFFSET ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, ROWS_PER_PAGE);
            ps.setInt(2, offset);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Machine(
                        rs.getInt("id"),
                        rs.getString("machine_code"),
                        rs.getString("machine_name"),
                        rs.getString("category"),
                        rs.getString("manufacturer"),
                        rs.getString("description")
                ));
            }

            machineTable.setItems(list);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getTotalMachineCount() {
        String sql = "SELECT COUNT(*) FROM machine";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getInt(1);
        } catch (SQLException e) {
            return 0;
        }
    }

    // ================= SAVE / UPDATE =================
    private void saveOrUpdateMachine() {

        if (!validateForm()) return;

        if (isUpdateMode) {

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Update");
            confirm.setHeaderText("Edit Machine");
            confirm.setContentText("Are you sure you want to update this machine?");

            if (confirm.showAndWait().get() == ButtonType.OK) {
                updateMachine();
            } else {
                return; // User cancelled
            }

        } else {
            insertMachine();
        }

        loadPagination();
        clearForm();
    }

    private void insertMachine() {
        String sql = "INSERT INTO machine (machine_code, machine_name, category, manufacturer, description) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, machineCodeField.getText().trim());
            ps.setString(2, nameField.getText().trim());
            ps.setString(3, categoryField.getText().trim());
            ps.setString(4, manufacturerField.getText().trim());
            ps.setString(5, descriptionField.getText().trim());

            ps.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Machine added successfully!");

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Machine Code already exists.");
        }
    }

    private void updateMachine() {
        String sql = "UPDATE machine SET machine_code=?, machine_name=?, category=?, manufacturer=?, description=? WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, machineCodeField.getText().trim());
            ps.setString(2, nameField.getText().trim());
            ps.setString(3, categoryField.getText().trim());
            ps.setString(4, manufacturerField.getText().trim());
            ps.setString(5, descriptionField.getText().trim());
            ps.setInt(6, selectedMachineId);

            ps.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Updated", "Machine updated successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================= DELETE =================
    private void deleteMachine() {

        Machine selected = machineTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String sql = "DELETE FROM machine WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, selected.getId());
            ps.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Deleted", "Machine deleted successfully!");
            loadPagination();
            clearForm();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================= LOAD SELECTED =================
    private void loadSelectedMachineData() {

        Machine selected = machineTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        selectedMachineId = selected.getId();

        machineCodeField.setText(selected.getMachineCode());
        nameField.setText(selected.getMachineName());
        categoryField.setText(selected.getCategory());
        manufacturerField.setText(selected.getManufacturer());
        descriptionField.setText(selected.getDescription());

        loadStockSummary(selectedMachineId);

        // Disable editing until Edit button clicked
        setFormEditable(false);
    }

    private void enableEditMode() {

        Machine selected = machineTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a machine first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Edit");
        confirm.setHeaderText("Edit Machine");
        confirm.setContentText("Do you want to edit this machine?");

        if (confirm.showAndWait().get() == ButtonType.OK) {

            isUpdateMode = true;
            setFormEditable(true);
            saveBtn.setText("Update Machine");

        }
    }

    private void setFormEditable(boolean status) {

        machineCodeField.setEditable(status);
        nameField.setEditable(status);
        categoryField.setEditable(status);
        manufacturerField.setEditable(status);
        descriptionField.setEditable(status);
    }

    // ================= STOCK SUMMARY =================
    private void loadStockSummary(int machineId) {

        try {
            totalUnitsLabel.setText("Total Units: " + getCount(machineId, null));
            availableUnitsLabel.setText("Available: " + getCount(machineId, "Available"));
            rentedUnitsLabel.setText("Rented: " + getCount(machineId, "Rented"));
            maintenanceUnitsLabel.setText("Maintenance: " + getCount(machineId, "Maintenance"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getCount(int machineId, String status) throws SQLException {

        String sql = (status == null)
                ? "SELECT COUNT(*) FROM machine_unit WHERE machine_id=?"
                : "SELECT COUNT(*) FROM machine_unit WHERE machine_id=? AND status=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, machineId);
            if (status != null) ps.setString(2, status);

            ResultSet rs = ps.executeQuery();
            return rs.getInt(1);
        }
    }

    // ================= VALIDATION =================
    private boolean validateForm() {

        if (machineCodeField.getText().trim().isEmpty()
                || nameField.getText().trim().isEmpty()) {

            showAlert(Alert.AlertType.WARNING, "Validation", "Required fields missing.");
            return false;
        }

        return true;
    }

    private void clearForm() {

        machineCodeField.clear();
        nameField.clear();
        categoryField.clear();
        manufacturerField.clear();
        descriptionField.clear();

        isUpdateMode = false;
        selectedMachineId = -1;

        saveBtn.setText("Save Machine");
        setFormEditable(true);
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}