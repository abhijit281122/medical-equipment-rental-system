package allController;

import allModels.MachineUnit;
import dbUtils.Database;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import java.sql.*;
import java.time.LocalDate;

public class MachineUnitController {

    @FXML private ComboBox<String> machineComboBox;
    @FXML private TextField serialField;
    @FXML private TextField lotField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextField conditionField;
    @FXML private DatePicker purchaseDatePicker;

    @FXML private TableView<MachineUnit> unitTable;
    @FXML private TableColumn<MachineUnit, Integer> colId;
    @FXML private TableColumn<MachineUnit, String> colMachine;
    @FXML private TableColumn<MachineUnit, String> colSerial;
    @FXML private TableColumn<MachineUnit, String> colLot;
    @FXML private TableColumn<MachineUnit, String> colStatus;
    @FXML private TableColumn<MachineUnit, String> colCondition;
    @FXML private TableColumn<MachineUnit, LocalDate> colDate;

    @FXML private Pagination pagination;

    private final Connection connection = Database.getInstance().getConnection();
    private static final int ROWS_PER_PAGE = 10;

    private int selectedId = -1;
    private boolean updateMode = false;

    @FXML
    public void initialize() {

        statusComboBox.setItems(FXCollections.observableArrayList(
                "Available", "Rented", "Maintenance"
        ));

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMachine.setCellValueFactory(new PropertyValueFactory<>("machineName"));
        colSerial.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        colLot.setCellValueFactory(new PropertyValueFactory<>("lotNumber"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colCondition.setCellValueFactory(new PropertyValueFactory<>("condition"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("purchaseDate"));

        applyStatusColorStyling();

        unitTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> loadSelected(newVal));

        loadMachines();
        loadPagination();
    }

    // ================== CRUD ==================

    @FXML
    private void saveUnit() {
        if (!validate()) return;

        if (updateMode) updateUnit();
        else insertUnit();

        loadPagination();
        clearForm();
    }

    private void insertUnit() {
        String sql = "INSERT INTO machine_unit (machine_id, serial_number, lot_number, status, condition, purchase_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, extractMachineId());
            ps.setString(2, serialField.getText().trim());
            ps.setString(3, lotField.getText().trim());
            ps.setString(4, statusComboBox.getValue());
            ps.setString(5, conditionField.getText().trim());
            ps.setDate(6, purchaseDatePicker.getValue() != null ?
                    Date.valueOf(purchaseDatePicker.getValue()) : null);

            ps.executeUpdate();

        } catch (SQLException e) {
            showError("Serial number already exists.");
        }
    }

    private void updateUnit() {
        String sql = "UPDATE machine_unit SET machine_id=?, serial_number=?, lot_number=?, status=?, condition=?, purchase_date=? WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, extractMachineId());
            ps.setString(2, serialField.getText());
            ps.setString(3, lotField.getText());
            ps.setString(4, statusComboBox.getValue());
            ps.setString(5, conditionField.getText());
            ps.setDate(6, purchaseDatePicker.getValue() != null ?
                    Date.valueOf(purchaseDatePicker.getValue()) : null);
            ps.setInt(7, selectedId);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteUnit() {

        MachineUnit selected = unitTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if ("Rented".equals(selected.getStatus())) {
            showError("Cannot delete a rented unit.");
            return;
        }

        String sql = "UPDATE machine_unit SET deleted=1 WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, selected.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        loadPagination();
    }

    // ================== PAGINATION ==================

    private void loadPagination() {
        int totalRows = getTotalCount();
        int pageCount = (int) Math.ceil((double) totalRows / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount);

        pagination.setPageFactory(this::createPage);
    }

    private TableView<MachineUnit> createPage(int pageIndex) {
        loadUnits(pageIndex * ROWS_PER_PAGE);
        return unitTable;
    }

    private void loadUnits(int offset) {

        ObservableList<MachineUnit> list = FXCollections.observableArrayList();

        String sql = "SELECT mu.*, m.machine_name FROM machine_unit mu JOIN machine m ON mu.machine_id = m.id WHERE mu.deleted=0 LIMIT ? OFFSET ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, ROWS_PER_PAGE);
            ps.setInt(2, offset);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new MachineUnit(
                        rs.getInt("id"),
                        rs.getInt("machine_id"),
                        rs.getString("machine_name"),
                        rs.getString("serial_number"),
                        rs.getString("lot_number"),
                        rs.getString("status"),
                        rs.getString("condition"),
                        rs.getDate("purchase_date") != null ?
                                rs.getDate("purchase_date").toLocalDate() : null
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        unitTable.setItems(list);
    }

    private int getTotalCount() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT COUNT(*) FROM machine_unit WHERE deleted=0")) {
            return rs.getInt(1);
        } catch (SQLException e) {
            return 0;
        }
    }

    // ================== FILTER ==================

    @FXML
    private void filterByMachine() {
        loadPagination();
    }

    // ================== STATUS COLOR ==================

    private void applyStatusColorStyling() {

        colStatus.setCellFactory(column -> new TableCell<MachineUnit, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);

                    switch (status) {
                        case "Available":
                            setTextFill(Color.GREEN);
                            break;
                        case "Rented":
                            setTextFill(Color.RED);
                            break;
                        case "Maintenance":
                            setTextFill(Color.ORANGE);
                            break;
                    }
                }
            }
        });
    }

    // ================== UTIL ==================

    private void loadSelected(MachineUnit unit) {
        if (unit == null) return;

        selectedId = unit.getId();
        updateMode = true;

        serialField.setText(unit.getSerialNumber());
        lotField.setText(unit.getLotNumber());
        statusComboBox.setValue(unit.getStatus());
        conditionField.setText(unit.getCondition());
        purchaseDatePicker.setValue(unit.getPurchaseDate());
    }

    @FXML
    private void clearForm() {
        serialField.clear();
        lotField.clear();
        conditionField.clear();
        purchaseDatePicker.setValue(null);
        updateMode = false;
        selectedId = -1;
    }

    private boolean validate() {
        return serialField.getText() != null &&
                !serialField.getText().trim().isEmpty();
    }

    private int extractMachineId() {
        return Integer.parseInt(
                machineComboBox.getValue().split(" - ")[0]
        );
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void loadMachines() {

        ObservableList<String> machineList = FXCollections.observableArrayList();

        String sql = "SELECT id, machine_name FROM machine WHERE deleted = 0 ORDER BY machine_name";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Format: id - name (needed for extractMachineId())
                machineList.add(
                        rs.getInt("id") + " - " + rs.getString("machine_name")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        machineComboBox.setItems(machineList);
    }
}