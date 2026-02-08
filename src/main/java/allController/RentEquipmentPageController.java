package allController;

import allModels.Equipment;
import allModels.Rental;
import dbUtils.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class RentEquipmentPageController implements Initializable {

    @FXML
    private ComboBox<Equipment> equipmentNameDropdown;
    @FXML
    private TextField equipmentIdField;
    @FXML
    private TextField patientNameField;
    @FXML
    private TextField challanNoField;
    @FXML
    private TextField staffSeparationField;
    @FXML
    private DatePicker rentReceivedDatePicker;
    @FXML
    private DatePicker rentCollectionDatePicker;
    @FXML
    private DatePicker rentEndDatePicker;
    @FXML
    private TextField collectedByField;

    @FXML
    private TableView<Rental> rentTable;
    @FXML
    private TableColumn<Rental, String> colEquipment;
    @FXML
    private TableColumn<Rental, String> colMachineNo;
    @FXML
    private TableColumn<Rental, String> colPatient;
    @FXML
    private TableColumn<Rental, String> colStatus;

    @FXML private TableColumn<Rental, String> colChallan;
    @FXML private TableColumn<Rental, String> colStaff;
    @FXML private TableColumn<Rental, String> colReceivedDate;
    @FXML private TableColumn<Rental, String> colCollectionDate;
    @FXML private TableColumn<Rental, String> colEndDate;
    @FXML private TableColumn<Rental, String> colCollector;
    @FXML private TableColumn<Rental, Double> colTotalAmount;
    @FXML private TableColumn<Rental, Double> colPaidAmount;
    @FXML private TableColumn<Rental, Double> colDueAmount;


    @FXML
    private TextField totalAmountField;
    @FXML
    private TextField paidAmountField;
    @FXML
    private TextField dueAmountField;

    @FXML
    private Button rentAddBtn;
    @FXML
    private Button rentClearBtn;
    @FXML
    private Button rentSubmitBtn;
    @FXML
    private Button rentPrintBtn;

    private final ObservableList<Equipment> equipmentList = FXCollections.observableArrayList();
    private final ObservableList<Rental> rentalList = FXCollections.observableArrayList();
    private Connection connection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connection = Database.getInstance().getConnection();

        loadEquipmentData();
        setupComboBox();
        setupTableColumns();

        rentAddBtn.setOnAction(e -> addToTable());
        rentClearBtn.setOnAction(e -> clearForm());
        rentSubmitBtn.setOnAction(e -> saveRentals());

        // Update due amount when paid amount is entered
        paidAmountField.textProperty().addListener((obs, oldVal, newVal) -> updateDueAmount());

        rentTable.setFixedCellSize(32);
        rentTable.setMinHeight(32 * 6 + 30);
        rentTable.setPrefHeight(32 * 6 + 30);
        rentTable.setMaxHeight(32 * 10 + 30);
    }

    // ---------------- LOAD EQUIPMENT ----------------
    private void loadEquipmentData() {
        equipmentList.clear();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM equipment");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Equipment e = new Equipment(
                        rs.getString("equipment_id"),      // equipment ID
                        rs.getString("equipment_name"),    // equipment name
                        rs.getString("condition"),         // condition
                        rs.getString("category"),          // category
                        rs.getString("status"),            // status (Available/Not Available)
                        rs.getDouble("rental_price")       // rental price
                );
                equipmentList.add(e);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void setupComboBox() {
        equipmentNameDropdown.setItems(equipmentList);
        equipmentNameDropdown.setConverter(new javafx.util.StringConverter<Equipment>() {
            @Override
            public String toString(Equipment equipment) {
                return equipment == null ? "" : equipment.getName();
            }

            @Override
            public Equipment fromString(String string) {
                return null;
            }
        });

        // Populate Machine No on selection
        equipmentNameDropdown.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                equipmentIdField.setText(newVal.getEquipmentId());
            } else {
                equipmentIdField.clear();
            }
        });
    }

    // ---------------- TABLE SETUP ----------------
    private void setupTableColumns() {
        colEquipment.setCellValueFactory(cell -> cell.getValue().machineNameProperty());
        colMachineNo.setCellValueFactory(cell -> cell.getValue().equipmentIdProperty());
        colPatient.setCellValueFactory(cell -> cell.getValue().patientNameProperty());
        colChallan.setCellValueFactory(cell -> cell.getValue().challanNoProperty());
        colStaff.setCellValueFactory(cell -> cell.getValue().staffSeparationProperty());
        colReceivedDate.setCellValueFactory(cell -> cell.getValue().rentReceivedDateProperty());
        colCollectionDate.setCellValueFactory(cell -> cell.getValue().rentCollectionDateProperty());
        colEndDate.setCellValueFactory(cell -> cell.getValue().rentEndDateProperty());
        colCollector.setCellValueFactory(cell -> cell.getValue().collectedByProperty());
        colTotalAmount.setCellValueFactory(cell -> cell.getValue().totalAmountProperty().asObject());
        colPaidAmount.setCellValueFactory(cell -> cell.getValue().paidAmountProperty().asObject());
        colDueAmount.setCellValueFactory(cell -> cell.getValue().dueAmountProperty().asObject());

        rentTable.setItems(rentalList);
        rentTable.setPlaceholder(new Label("No rentals available."));
    }

    // ---------------- ADD TO TABLE ----------------
    private void addToTable() {
        Equipment selected = equipmentNameDropdown.getValue();
        if (selected == null || patientNameField.getText().isEmpty()) {
            showAlert("Please select a machine and enter patient name.");
            return;
        }

        double amount = selected.getRentalPrice();

        Rental r = new Rental(
                0,
                equipmentIdField.getText(),
                selected.getName(),
                patientNameField.getText(),
                challanNoField.getText(),
                staffSeparationField.getText(),
                rentReceivedDatePicker.getValue() != null ? rentReceivedDatePicker.getValue().toString() : "",
                rentCollectionDatePicker.getValue() != null ? rentCollectionDatePicker.getValue().toString() : "",
                rentEndDatePicker.getValue() != null ? rentEndDatePicker.getValue().toString() : "",
                "Active",
                collectedByField.getText(),
                amount,     // amount
                amount,     // total amount initially same as amount
                0,          // paid amount
                amount,     // due amount initially = amount
                ""          // period
        );

        rentalList.add(r);
        updateTotalAmount();
        clearForm();
    }

    // ---------------- UPDATE TOTAL AMOUNT ----------------
    private void updateTotalAmount() {
        double total = rentalList.stream().mapToDouble(Rental::getAmount).sum();
        totalAmountField.setText(String.format("%.2f", total));
        updateDueAmount();
    }

    // ---------------- UPDATE DUE AMOUNT ----------------
    private void updateDueAmount() {
        try {
            double total = Double.parseDouble(totalAmountField.getText());
            double paid = paidAmountField.getText().isEmpty() ? 0 : Double.parseDouble(paidAmountField.getText());
            double due = total - paid;
            dueAmountField.setText(String.format("%.2f", due));
        } catch (NumberFormatException e) {
            dueAmountField.setText("0.00");
        }
    }

    // ---------------- CLEAR FORM ----------------
    private void clearForm() {
        equipmentNameDropdown.getSelectionModel().clearSelection();
        equipmentIdField.clear();
        patientNameField.clear();
        challanNoField.clear();
        staffSeparationField.clear();
        rentReceivedDatePicker.setValue(null);
        rentCollectionDatePicker.setValue(null);
        rentEndDatePicker.setValue(null);
        collectedByField.clear();
    }

    // ---------------- SAVE RENTALS ----------------
    private void saveRentals() {
        if (rentalList.isEmpty()) {
            showAlert("No rentals to save.");
            return;
        }

        try {
            // Updated SQL to include machine_name and amount
            String sql = "INSERT INTO rental (equipment_id, machine_name, patient_name, challan_no, staff_separation, rent_received_date, rent_collection_date, rent_end_date, status, collected_by, amount, total_amount, paid_amount, due_amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);

            double paid = paidAmountField.getText().isEmpty() ? 0 : Double.parseDouble(paidAmountField.getText());

            for (Rental r : rentalList) {
                ps.setString(1, r.getEquipmentId());
                ps.setString(2, r.getMachineName());      // machine_name
                ps.setString(3, r.getPatientName());
                ps.setString(4, r.getChallanNo());
                ps.setString(5, r.getStaffSeparation());
                ps.setString(6, r.getRentReceivedDate());
                ps.setString(7, r.getRentCollectionDate());
                ps.setString(8, r.getRentEndDate());
                ps.setString(9, r.getStatus());
                ps.setString(10, r.getCollectedBy());
                ps.setDouble(11, r.getAmount());         // unit rental price
                ps.setDouble(12, r.getTotalAmount());
                ps.setDouble(13, paid);
                ps.setDouble(14, r.getTotalAmount() - paid); // due
                ps.addBatch();
            }

            ps.executeBatch();
            showAlert("Rentals saved successfully!");
            rentalList.clear();
            totalAmountField.clear();
            paidAmountField.clear();
            dueAmountField.clear();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error saving rentals: " + e.getMessage());
        }
    }


    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
