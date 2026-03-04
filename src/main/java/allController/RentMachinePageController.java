package allController;

import allModels.Rental;
import allModels.RentalTableModel;
import dbUtils.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;
import java.util.UUID;

public class RentMachinePageController implements Initializable {

    // ================= FORM FIELDS =================

    @FXML
    private ComboBox<String> machineDropdown;
    @FXML
    private ComboBox<String> machineUnitDropdown;
    @FXML
    private ComboBox<String> customerDropdown;
    @FXML
    private TextField customerAddressField;
    @FXML
    private TextField customerMobileField;
    @FXML
    private TextField customerLocationField;
    @FXML
    private TextField challanField;
    @FXML
    private ComboBox<String> staffDropdown;
    @FXML
    private DatePicker rentStartDatePicker;
    @FXML
    private DatePicker rentEndDatePicker;
    @FXML
    private DatePicker actualReturnDatePicker;
    @FXML
    private ComboBox<String> rentCollectedByDropdown;
    @FXML
    private TextField totalAmountField;
    @FXML
    private TextField paidAmountField;
    @FXML
    private TextField dueAmountField;
    @FXML
    private TextField priceField;

    // ================= TABLE =================

    @FXML
    private TableView<RentalTableModel> rentalTable;
    @FXML
    private TableColumn<RentalTableModel, Number> colSerial;
    @FXML
    private TableColumn<RentalTableModel, String> colMachineInfo;
    @FXML
    private TableColumn<RentalTableModel, Number> colPrice;
    @FXML
    private TableColumn<RentalTableModel, String> colCustomerName;
    @FXML
    private TableColumn<RentalTableModel, LocalDate> colStartDate;
    @FXML
    private TableColumn<RentalTableModel, LocalDate> colEndDate;

    @FXML
    private Button addToCartBtn;
    @FXML
    private Button saveBtn;

    private final ObservableList<RentalTableModel> cartList = FXCollections.observableArrayList();
    private final ObservableList<Rental> rentalEntities = FXCollections.observableArrayList();

    private final ObservableList<String> machineList = FXCollections.observableArrayList();
    private final ObservableList<String> machineUnitList = FXCollections.observableArrayList();
    private final ObservableList<String> customerList = FXCollections.observableArrayList();
    private final ObservableList<String> employeeList = FXCollections.observableArrayList();

    private Connection connection;
    private int serialCounter = 1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        connection = Database.getInstance().getConnection();

        setupTable();
        generateChallan();

        totalAmountField.setEditable(false);
        dueAmountField.setEditable(false);

        paidAmountField.textProperty().addListener((obs, o, n) -> calculateDue());

        addToCartBtn.setOnAction(e -> addToCart());
        saveBtn.setOnAction(e -> saveRentals());

        loadMachines();
        loadCustomers();
        loadEmployees();

        machineDropdown.setOnAction(e -> loadMachineUnits());
        customerDropdown.setOnAction(e -> fetchCustomerDetails());
    }

    // ================= TABLE SETUP =================

    private void setupTable() {

        colSerial.setCellValueFactory(cell -> cell.getValue().serialNoProperty());
        colMachineInfo.setCellValueFactory(cell -> cell.getValue().machineInfoProperty());
        colPrice.setCellValueFactory(cell -> cell.getValue().totalAmountProperty());
        colCustomerName.setCellValueFactory(cell -> cell.getValue().customerNameProperty());
        colStartDate.setCellValueFactory(cell -> cell.getValue().rentStartDateProperty());
        colEndDate.setCellValueFactory(cell -> cell.getValue().rentEndDateProperty());

        rentalTable.setItems(cartList);
    }

    // ================= ADD TO CART =================

    private void addToCart() {

        try {

            if (machineUnitDropdown.getValue() == null ||
                    customerDropdown.getValue() == null ||
                    rentStartDatePicker.getValue() == null ||
                    rentEndDatePicker.getValue() == null ||
                    priceField.getText().isEmpty()) {

                showAlert("Please fill all required fields including price.");
                return;
            }

            long daysBetween = ChronoUnit.DAYS.between(
                    rentStartDatePicker.getValue(),
                    rentEndDatePicker.getValue()
            );

            if (daysBetween < 0) {
                showAlert("End date must be after start date!");
                return;
            }

            int totalDays = (int) daysBetween + 1;

            double enteredPrice = Double.parseDouble(priceField.getText());

            int machineUnitId = getMachineUnitId(machineUnitDropdown.getValue());
            int customerId = getCustomerId(customerDropdown.getValue());
            int deliveryId = getEmployeeId(staffDropdown.getValue());
            int collectorId = getEmployeeId(rentCollectedByDropdown.getValue());

            // ===== CREATE RENTAL ENTITY =====

            Rental rental = new Rental();
            rental.setChallanNo(challanField.getText());
            rental.setMachineUnitId(machineUnitId);
            rental.setCustomerId(customerId);
            rental.setDeliveryPersonId(deliveryId);
            rental.setRentCollectorId(collectorId);
            rental.setRentStartDate(rentStartDatePicker.getValue());
            rental.setRentEndDate(rentEndDatePicker.getValue());
            rental.setPrice(enteredPrice);
            rental.setDays(totalDays);
            rental.setTotalAmount(enteredPrice);
            rental.setStatus("ACTIVE");

            rentalEntities.add(rental);

            // ===== TABLE MODEL =====

            RentalTableModel tableModel = new RentalTableModel(
                    serialCounter++,
                    machineDropdown.getValue() + " - " + machineUnitDropdown.getValue(),
                    enteredPrice,
                    customerDropdown.getValue(),
                    rentStartDatePicker.getValue(),
                    rentEndDatePicker.getValue()
            );

            cartList.add(tableModel);

            recalculateGrandTotal();
            clearForm();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= SAVE TO DATABASE =================

    private void saveRentals() {

        try {

            String sql = "INSERT INTO rental (challan_no, machine_unit_id, customer_id, " +
                    "delivery_person_id, rent_collector_id, rent_start_date, expected_return_date, " +
                    "days, total_amount, paid_amount, due_amount, status, price) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

            PreparedStatement ps = connection.prepareStatement(sql);

            double grandTotal = Double.parseDouble(totalAmountField.getText());

            double paid = paidAmountField.getText().isEmpty()
                    ? 0 : Double.parseDouble(paidAmountField.getText());
            double due = grandTotal - paid;

            for (Rental r : rentalEntities) {

                // 🔥 Set values BEFORE saving
                r.setPaidAmount(paid);
                r.setDueAmount(due);

                ps.setString(1, r.getChallanNo());
                ps.setInt(2, r.getMachineUnitId());
                ps.setInt(3, r.getCustomerId());
                ps.setInt(4, r.getDeliveryPersonId());
                ps.setInt(5, r.getRentCollectorId());
                ps.setDate(6, Date.valueOf(r.getRentStartDate()));
                ps.setDate(7, Date.valueOf(r.getRentEndDate()));
                ps.setInt(8, r.getDays());
                ps.setDouble(9, r.getTotalAmount());
                ps.setDouble(10, r.getPaidAmount());
                ps.setDouble(11, r.getDueAmount());
                ps.setString(12, r.getStatus());
                ps.setDouble(13, r.getPrice());

                ps.addBatch();

                // Update machine status
                PreparedStatement update = connection.prepareStatement(
                        "UPDATE machine_unit SET status='RENTED' WHERE id=?");
                update.setInt(1, r.getMachineUnitId());
                update.executeUpdate();
            }

            ps.executeBatch();

            showAlert("Rental Saved Successfully!");

            cartList.clear();
            rentalEntities.clear();
            serialCounter = 1;
            clearForm();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= HELPER METHODS =================

    private void generateChallan() {
        challanField.setText("CH-" + UUID.randomUUID().toString().substring(0, 8));
    }

    private void calculateDue() {

        try {

            double total = totalAmountField.getText().isEmpty()
                    ? 0 : Double.parseDouble(totalAmountField.getText());

            double paid = paidAmountField.getText().isEmpty()
                    ? 0 : Double.parseDouble(paidAmountField.getText());

            dueAmountField.setText(String.valueOf(total - paid));

        } catch (Exception ignored) {
        }
    }

    private void clearForm() {

        machineDropdown.getSelectionModel().clearSelection();
        machineUnitDropdown.getSelectionModel().clearSelection();
        customerDropdown.getSelectionModel().clearSelection();
        staffDropdown.getSelectionModel().clearSelection();
        rentCollectedByDropdown.getSelectionModel().clearSelection();

        rentStartDatePicker.setValue(null);
        rentEndDatePicker.setValue(null);
        actualReturnDatePicker.setValue(null);

        customerAddressField.clear();
        customerMobileField.clear();
        customerLocationField.clear();

        priceField.clear();
/*        paidAmountField.clear();
        dueAmountField.clear();
        totalAmountField.clear();*/

        generateChallan(); // generate new challan after save
    }

    // ======= DB LOOKUP HELPERS =======

    private int getMachineUnitId(String serialNumber) throws Exception {

        String sql = "SELECT id FROM machine_unit WHERE serial_number=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, serialNumber);

        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getInt("id") : 0;
    }

    private int getCustomerId(String name) throws Exception {
        PreparedStatement ps = connection.prepareStatement(
                "SELECT id FROM customer WHERE full_name=?");
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getInt("id") : 0;
    }

    private int getEmployeeId(String name) throws Exception {
        PreparedStatement ps = connection.prepareStatement(
                "SELECT id FROM employee WHERE full_name=?");
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getInt("id") : 0;
    }

    private double getMachinePrice(int machineUnitId) throws Exception {

        String sql =
                "SELECT m.rental_price_per_day " +
                        "FROM machine_unit mu " +
                        "JOIN machine m ON mu.machine_id = m.id " +
                        "WHERE mu.id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, machineUnitId);

        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getDouble("rental_price_per_day") : 0;
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void loadMachines() {

        machineList.clear();

        try {
            String sql = "SELECT machine_name FROM machine WHERE deleted=0";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                machineList.add(rs.getString("machine_name"));
            }

            machineDropdown.setItems(machineList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMachineUnits() {

        machineUnitList.clear();

        try {
            String sql =
                    "SELECT mu.serial_number " +
                            "FROM machine_unit mu " +
                            "JOIN machine m ON mu.machine_id = m.id " +
                            "WHERE TRIM(m.machine_name) = TRIM(?) " +
                            "AND mu.status='Available'";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, machineDropdown.getValue());

            System.out.println("Selected Machine: '" + machineDropdown.getValue() + "'");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                machineUnitList.add(rs.getString("serial_number"));
            }

            machineUnitDropdown.setItems(machineUnitList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCustomers() {

        customerList.clear();

        try {
            String sql = "SELECT full_name FROM customer WHERE is_deleted=0";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                customerList.add(rs.getString("full_name"));
            }

            customerDropdown.setItems(customerList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadEmployees() {

        employeeList.clear();

        try {
            String sql = "SELECT full_name FROM employee WHERE is_deleted=0";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                employeeList.add(rs.getString("full_name"));
            }

            staffDropdown.setItems(employeeList);
            rentCollectedByDropdown.setItems(employeeList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchCustomerDetails() {

        if (customerDropdown.getValue() == null) return;

        try {
            String sql = "SELECT address, location, phone FROM customer WHERE full_name=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, customerDropdown.getValue());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                customerAddressField.setText(rs.getString("address"));
                customerMobileField.setText(rs.getString("phone"));
                customerLocationField.setText(rs.getString("location"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recalculateGrandTotal() {

        double total = 0;

        for (RentalTableModel item : cartList) {
            total += item.getTotalAmount();
        }

        totalAmountField.setText(String.valueOf(total));
        calculateDue();
    }
}