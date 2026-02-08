package allController;

import allModels.Equipment;
import allModels.Rental;
import allModels.WeeklyCollectionRow;
import dbUtils.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    // SIDEBAR BUTTONS
    @FXML
    private Button homeBtn;
    @FXML
    private Button availableEquipmentBtn;
    @FXML
    private Button rentEquipmentBtn;

    // TOP BAR
    @FXML
    private Label usernameLabel;
    @FXML
    private Button logoutBtn;

    // MAIN CONTENT PAGES
    @FXML
    private StackPane mainContent;
    @FXML
    private VBox homePage;
    @FXML
    private VBox availableEquipmentPage;
    @FXML
    private VBox rentEquipmentPage;

    @FXML
    private VBox equipmentListPage;
    @FXML
    private VBox equipmentAddPage;

    // DASHBOARD LABELS
    @FXML
    private Label totalEquipmentLabel;
    @FXML
    private Label availableEquipmentLabel;
    @FXML
    private Label totalRentalsLabel;
    @FXML
    private Label activeRentsLabel;
    @FXML
    private Label closedRentsLabel;
    @FXML
    private Label dueWithinPeriodLabel;
    @FXML
    private Label weeklyCollectionLabel;

    // EMPLOYEE
    @FXML
    private Button employeeBtn;

    @FXML
    private VBox employeePage;

    // CHART
    @FXML
    private BarChart<String, Number> equipmentChart;

    // TABLES
    @FXML
    private TableView<Equipment> equipmentTable;
    @FXML
    private TableView<Rental> rentTable;

    // ACTIVE RENT PAGE
    @FXML
    private VBox activeRentPage;

    @FXML
    private TableView<Rental> activeRentTable;

    // CLOSED RENT PAGE
    @FXML
    private VBox closedRentPage;

    @FXML
    private TableView<Rental> closedRentTable;

    // DUE RENT PAGE
    @FXML
    private VBox dueRentPage;

    @FXML
    private TableView<Rental> dueRentTable;

    //WEEKLY
    @FXML
    private VBox weeklyCollectionPage;

    @FXML
    private TableView<WeeklyCollectionRow> weeklyCollectionTable;

    @FXML
    private BarChart<String, Number> weeklyCollectionChart;



    // SEARCH FIELDS
    @FXML
    private TextField equipmentSearchField;
    @FXML
    private TextField rentSearchField;

    // DATA LISTS
    private final ObservableList<Equipment> equipmentList = FXCollections.observableArrayList();
    private final ObservableList<Rental> rentalList = FXCollections.observableArrayList();

    private static DashboardController instance;

    public DashboardController() {
        instance = this;
    }


    private Connection connection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connection = Database.getInstance().getConnection();

        hideAllPages();
        homePage.setVisible(true);

        // Default page
        if (homePage != null) showPage(homePage);

        // Load data
        if (equipmentTable != null) loadEquipmentData();
        if (rentTable != null) loadRentalData();

        // Dashboard stats and chart
        if (totalEquipmentLabel != null && availableEquipmentLabel != null && totalRentalsLabel != null)
            updateDashboardStats();

        if (equipmentChart != null) populateChart();

        // Button actions
        if (homeBtn != null) homeBtn.setOnAction(e -> showPage(homePage));
        if (availableEquipmentBtn != null) availableEquipmentBtn.setOnAction(e -> showPage(availableEquipmentPage));
        if (rentEquipmentBtn != null) rentEquipmentBtn.setOnAction(e -> onRentEquipmentBtnClick());
        if (employeeBtn != null) employeeBtn.setOnAction(e -> openEmployeePage());
        if (logoutBtn != null) logoutBtn.setOnAction(e -> logout());

        // Table search
        if (equipmentSearchField != null && rentSearchField != null) setupSearchFilters();

        refreshEquipmentData();
    }

    // ------------------- PAGE SWITCHING -------------------
    private void showPage(VBox page) {
        if (homePage != null) homePage.setVisible(false);
        if (rentEquipmentPage != null) rentEquipmentPage.setVisible(false);
        if (equipmentAddPage != null) equipmentAddPage.setVisible(false);
        if (equipmentListPage != null) equipmentListPage.setVisible(false);

        if (page != null) page.setVisible(true);
    }

    // ------------------- LOAD DATA -------------------
    private void loadEquipmentData() {
        if (equipmentTable == null) return;

        equipmentList.clear();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM equipment");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Equipment e = new Equipment(
                        rs.getString("equipment_id"),
                        rs.getString("equipment_name"),
                        rs.getString("condition"),
                        rs.getString("category")
                );
                equipmentList.add(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (equipmentTable.getColumns().isEmpty()) {
            TableColumn<Equipment, String> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(cell -> cell.getValue().equipmentIdProperty());

            TableColumn<Equipment, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(cell -> cell.getValue().nameProperty());

            TableColumn<Equipment, String> categoryCol = new TableColumn<>("Category");
            categoryCol.setCellValueFactory(cell -> cell.getValue().categoryProperty());

            TableColumn<Equipment, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(cell -> cell.getValue().statusProperty());

            equipmentTable.getColumns().addAll(idCol, nameCol, categoryCol, statusCol);
        }

        equipmentTable.setItems(equipmentList);
    }

    private void loadRentalData() {
        if (rentTable == null) return;

        rentalList.clear();

        try {
            String sql = "SELECT r.id, r.equipment_id, e.equipment_name, r.patient_name, " +
                    "r.challan_no, r.staff_separation, r.rent_received_date, " +
                    "r.rent_collection_date, r.rent_end_date, r.status, r.collected_by " +
                    "FROM rental r JOIN equipment e ON r.equipment_id = e.equipment_id";

            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String received = rs.getString("rent_received_date");
                String end = rs.getString("rent_end_date");

                String period = "-";
                try {
                    if (received != null && end != null) {
                        long days = java.time.temporal.ChronoUnit.DAYS.between(
                                java.time.LocalDate.parse(received),
                                java.time.LocalDate.parse(end)
                        );
                        period = days + " days";
                    }
                } catch (Exception ex) {
                    period = "-";
                }

                Rental r = new Rental(
                        rs.getInt("id"),
                        rs.getString("equipment_id"),
                        rs.getString("equipment_name"),
                        rs.getString("patient_name"),
                        rs.getString("challan_no"),
                        rs.getString("staff_separation"),
                        received,
                        rs.getString("rent_collection_date"),
                        end,
                        rs.getString("status"),
                        rs.getString("collected_by"),
                        rs.getDouble("amount"),
                        rs.getDouble("totalAmount"),
                        rs.getDouble("paidAmount"),
                        rs.getDouble("dueAmount"),
                        period
                );

                rentalList.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (rentTable.getColumns().isEmpty()) {
            TableColumn<Rental, String> machineCol = new TableColumn<>("Machine");
            machineCol.setCellValueFactory(cell -> cell.getValue().machineNameProperty());

            TableColumn<Rental, String> numberCol = new TableColumn<>("Machine No");
            numberCol.setCellValueFactory(cell -> cell.getValue().equipmentIdProperty());

            TableColumn<Rental, String> patientCol = new TableColumn<>("Patient");
            patientCol.setCellValueFactory(cell -> cell.getValue().patientNameProperty());

            TableColumn<Rental, String> challanCol = new TableColumn<>("Challan No");
            challanCol.setCellValueFactory(cell -> cell.getValue().challanNoProperty());

            TableColumn<Rental, String> staffCol = new TableColumn<>("Staff Separation");
            staffCol.setCellValueFactory(cell -> cell.getValue().staffSeparationProperty());

            TableColumn<Rental, String> receivedCol = new TableColumn<>("Received");
            receivedCol.setCellValueFactory(cell -> cell.getValue().rentReceivedDateProperty());

            TableColumn<Rental, String> collectionCol = new TableColumn<>("Collection Date");
            collectionCol.setCellValueFactory(cell -> cell.getValue().rentCollectionDateProperty());

            TableColumn<Rental, String> endCol = new TableColumn<>("End Date");
            endCol.setCellValueFactory(cell -> cell.getValue().rentEndDateProperty());

            TableColumn<Rental, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(cell -> cell.getValue().statusProperty());

            TableColumn<Rental, String> collectedByCol = new TableColumn<>("Collected By");
            collectedByCol.setCellValueFactory(cell -> cell.getValue().collectedByProperty());

            TableColumn<Rental, String> periodCol = new TableColumn<>("Period");
            periodCol.setCellValueFactory(cell -> cell.getValue().periodProperty());

            rentTable.getColumns().addAll(
                    machineCol, numberCol, patientCol, challanCol, staffCol,
                    receivedCol, collectionCol, endCol, statusCol, collectedByCol, periodCol
            );
        }

        rentTable.setItems(rentalList);
    }

    private void setupActiveRentTable() {

        if (!activeRentTable.getColumns().isEmpty()) return;

        TableColumn<Rental, String> machineCol = new TableColumn<>("Machine");
        machineCol.setCellValueFactory(c -> c.getValue().machineNameProperty());

        TableColumn<Rental, String> equipmentCol = new TableColumn<>("Machine No");
        equipmentCol.setCellValueFactory(c -> c.getValue().equipmentIdProperty());

        TableColumn<Rental, String> patientCol = new TableColumn<>("Patient");
        patientCol.setCellValueFactory(c -> c.getValue().patientNameProperty());

        TableColumn<Rental, String> challanCol = new TableColumn<>("Challan No");
        challanCol.setCellValueFactory(c -> c.getValue().challanNoProperty());

        TableColumn<Rental, String> startCol = new TableColumn<>("Received Date");
        startCol.setCellValueFactory(c -> c.getValue().rentReceivedDateProperty());

        TableColumn<Rental, String> endCol = new TableColumn<>("End Date");
        endCol.setCellValueFactory(c -> c.getValue().rentEndDateProperty());

        TableColumn<Rental, String> periodCol = new TableColumn<>("Period");
        periodCol.setCellValueFactory(c -> c.getValue().periodProperty());

        activeRentTable.getColumns().addAll(
                machineCol, equipmentCol, patientCol,
                challanCol, startCol, endCol, periodCol
        );
    }

    private void loadActiveRentData() {
        setupActiveRentTable(); // Ensure table columns initialized

        ObservableList<Rental> activeList = FXCollections.observableArrayList();

        String sql =
                "SELECT r.id, r.equipment_id, e.equipment_name, r.patient_name, " +
                        "r.challan_no, r.staff_separation, r.rent_received_date, " +
                        "r.rent_collection_date, r.rent_end_date, r.status, r.collected_by, " +
                        "IFNULL(r.amount, 0) AS amount, " +
                        "IFNULL(r.total_amount, 0) AS totalAmount, " +
                        "IFNULL(r.paid_amount, 0) AS paidAmount, " +
                        "IFNULL(r.due_amount, 0) AS dueAmount " +
                        "FROM rental r " +
                        "JOIN equipment e ON r.equipment_id = e.equipment_id " +
                        "WHERE r.status = 'Active' " +
                        "ORDER BY r.rent_end_date";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String received = rs.getString("rent_received_date");
                String end = rs.getString("rent_end_date");
                String period = "-";

                if (received != null && !received.isEmpty() && end != null && !end.isEmpty()) {
                    try {
                        long days = java.time.temporal.ChronoUnit.DAYS.between(
                                java.time.LocalDate.parse(received),
                                java.time.LocalDate.parse(end)
                        );
                        period = days + " days";
                    } catch (Exception ignored) {}
                }

                activeList.add(new Rental(
                        rs.getInt("id"),
                        rs.getString("equipment_id"),
                        rs.getString("equipment_name"),
                        rs.getString("patient_name"),
                        rs.getString("challan_no"),
                        rs.getString("staff_separation"),
                        received,
                        rs.getString("rent_collection_date"),
                        end,
                        rs.getString("status"),
                        rs.getString("collected_by"),
                        rs.getDouble("amount"),
                        rs.getDouble("totalAmount"),
                        rs.getDouble("paidAmount"),
                        rs.getDouble("dueAmount"),
                        period
                ));
            }

            activeRentTable.setItems(activeList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void setupClosedRentTable() {

        if (!closedRentTable.getColumns().isEmpty()) return;

        TableColumn<Rental, String> machineCol = new TableColumn<>("Machine");
        machineCol.setCellValueFactory(c -> c.getValue().machineNameProperty());

        TableColumn<Rental, String> equipmentCol = new TableColumn<>("Machine No");
        equipmentCol.setCellValueFactory(c -> c.getValue().equipmentIdProperty());

        TableColumn<Rental, String> patientCol = new TableColumn<>("Patient");
        patientCol.setCellValueFactory(c -> c.getValue().patientNameProperty());

        TableColumn<Rental, String> challanCol = new TableColumn<>("Challan No");
        challanCol.setCellValueFactory(c -> c.getValue().challanNoProperty());

        TableColumn<Rental, String> receivedCol = new TableColumn<>("Received Date");
        receivedCol.setCellValueFactory(c -> c.getValue().rentReceivedDateProperty());

        TableColumn<Rental, String> collectedCol = new TableColumn<>("Collection Date");
        collectedCol.setCellValueFactory(c -> c.getValue().rentCollectionDateProperty());

        TableColumn<Rental, String> endCol = new TableColumn<>("End Date");
        endCol.setCellValueFactory(c -> c.getValue().rentEndDateProperty());

        TableColumn<Rental, String> collectedByCol = new TableColumn<>("Collected By");
        collectedByCol.setCellValueFactory(c -> c.getValue().collectedByProperty());

        closedRentTable.getColumns().addAll(
                machineCol, equipmentCol, patientCol,
                challanCol, receivedCol, collectedCol,
                endCol, collectedByCol
        );
    }

    private void loadClosedRentData() {

        setupClosedRentTable();

        ObservableList<Rental> closedList = FXCollections.observableArrayList();

        String sql =
                "SELECT r.id, r.equipment_id, e.equipment_name, r.patient_name, " +
                        "r.challan_no, r.staff_separation, r.rent_received_date, " +
                        "r.rent_collection_date, r.rent_end_date, r.status, r.collected_by, " +
                        "IFNULL(r.amount, 0) AS amount, " +
                        "IFNULL(r.total_amount, 0) AS totalAmount, " +
                        "IFNULL(r.paid_amount, 0) AS paidAmount, " +
                        "IFNULL(r.due_amount, 0) AS dueAmount " +
                        "FROM rental r " +
                        "JOIN equipment e ON r.equipment_id = e.equipment_id " +
                        "WHERE r.status = 'Closed' " +
                        "ORDER BY r.rent_collection_date DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                closedList.add(new Rental(
                        rs.getInt("id"),
                        rs.getString("equipment_id"),
                        rs.getString("equipment_name"),
                        rs.getString("patient_name"),
                        rs.getString("challan_no"),
                        rs.getString("staff_separation"),
                        rs.getString("rent_received_date"),
                        rs.getString("rent_collection_date"),
                        rs.getString("rent_end_date"),
                        rs.getString("status"),
                        rs.getString("collected_by"),
                        rs.getDouble("amount"),
                        rs.getDouble("totalAmount"),
                        rs.getDouble("paidAmount"),
                        rs.getDouble("dueAmount"),
                        "-"   // period not needed for closed
                ));
            }

            closedRentTable.setItems(closedList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setupDueRentTable() {

        if (!dueRentTable.getColumns().isEmpty()) return;

        TableColumn<Rental, String> machineCol = new TableColumn<>("Machine");
        machineCol.setCellValueFactory(c -> c.getValue().machineNameProperty());

        TableColumn<Rental, String> equipmentCol = new TableColumn<>("Machine No");
        equipmentCol.setCellValueFactory(c -> c.getValue().equipmentIdProperty());

        TableColumn<Rental, String> patientCol = new TableColumn<>("Patient");
        patientCol.setCellValueFactory(c -> c.getValue().patientNameProperty());

        TableColumn<Rental, String> challanCol = new TableColumn<>("Challan No");
        challanCol.setCellValueFactory(c -> c.getValue().challanNoProperty());

        TableColumn<Rental, String> receivedCol = new TableColumn<>("Received Date");
        receivedCol.setCellValueFactory(c -> c.getValue().rentReceivedDateProperty());

        TableColumn<Rental, String> endCol = new TableColumn<>("End Date");
        endCol.setCellValueFactory(c -> c.getValue().rentEndDateProperty());

        TableColumn<Rental, String> periodCol = new TableColumn<>("Due Days");
        periodCol.setCellValueFactory(c -> c.getValue().periodProperty());

        dueRentTable.getColumns().addAll(
                machineCol, equipmentCol, patientCol,
                challanCol, receivedCol, endCol, periodCol
        );
    }

    private void loadDueRentData() {

        setupDueRentTable();

        ObservableList<Rental> dueList = FXCollections.observableArrayList();

        String sql =
                "SELECT r.id, r.equipment_id, e.equipment_name, r.patient_name, " +
                        "r.challan_no, r.staff_separation, r.rent_received_date, " +
                        "r.rent_collection_date, r.rent_end_date, r.status, r.collected_by, " +
                        "IFNULL(r.amount, 0) AS amount, " +
                        "IFNULL(r.total_amount, 0) AS totalAmount, " +
                        "IFNULL(r.paid_amount, 0) AS paidAmount, " +
                        "IFNULL(r.due_amount, 0) AS dueAmount " +
                        "FROM rental r " +
                        "JOIN equipment e ON r.equipment_id = e.equipment_id " +
                        "WHERE r.status = 'Due'";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                String endDateStr = rs.getString("rent_end_date");
                long dueDays = calculateDueDays(endDateStr);

                dueList.add(new Rental(
                        rs.getInt("id"),
                        rs.getString("equipment_id"),
                        rs.getString("equipment_name"),
                        rs.getString("patient_name"),
                        rs.getString("challan_no"),
                        rs.getString("staff_separation"),
                        rs.getString("rent_received_date"),
                        rs.getString("rent_collection_date"),
                        endDateStr,
                        rs.getString("status"),
                        rs.getString("collected_by"),
                        rs.getDouble("amount"),
                        rs.getDouble("totalAmount"),
                        rs.getDouble("paidAmount"),
                        rs.getDouble("dueAmount"),
                        String.valueOf(dueDays)
                ));
            }

            dueRentTable.setItems(dueList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private long calculateDueDays(String endDateStr) {

        if (endDateStr == null || endDateStr.isEmpty()) return 0;

        LocalDate endDate = LocalDate.parse(endDateStr);
        LocalDate today = LocalDate.now();

        if (today.isBefore(endDate)) return 0;

        return ChronoUnit.DAYS.between(endDate, today);
    }

    // ------------------- DASHBOARD STATS -------------------
    private void updateDashboardStats() {

        if (totalEquipmentLabel != null)
            totalEquipmentLabel.setText(String.valueOf(equipmentList.size()));

        long availableCount = equipmentList.stream()
                .filter(e -> "Available".equalsIgnoreCase(e.getStatus()))
                .count();

        if (availableEquipmentLabel != null)
            availableEquipmentLabel.setText(String.valueOf(availableCount));

        if (totalRentalsLabel != null)
            totalRentalsLabel.setText(String.valueOf(getTotalRentals()));

        if (activeRentsLabel != null)
            activeRentsLabel.setText(String.valueOf(getActiveRents()));

        if (closedRentsLabel != null)
            closedRentsLabel.setText(String.valueOf(getClosedRents()));

        if (dueWithinPeriodLabel != null)
            dueWithinPeriodLabel.setText(String.valueOf(getDueWithinDays(7)));

        if (weeklyCollectionLabel != null)
            weeklyCollectionLabel.setText("₹ " + getWeeklyCollection());
    }


    private void populateChart() {
        if (equipmentChart == null) return;

        equipmentChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Available Equipment");

        equipmentList.forEach(e -> {
            int value = e.getStatus() != null && e.getStatus().equalsIgnoreCase("Available") ? 1 : 0;
            series.getData().add(new XYChart.Data<>(e.getName(), value));
        });

        equipmentChart.getData().add(series);
    }

    // ------------------- SEARCH FILTERS -------------------
    private void setupSearchFilters() {
        if (equipmentSearchField != null) {
            FilteredList<Equipment> filteredEquipments = new FilteredList<>(equipmentList, p -> true);
            equipmentSearchField.textProperty().addListener((obs, oldVal, newVal) ->
                    filteredEquipments.setPredicate(e -> e.getName() != null &&
                            e.getName().toLowerCase().contains(newVal.toLowerCase()))
            );
            SortedList<Equipment> sortedEquipments = new SortedList<>(filteredEquipments);
            if (equipmentTable != null) {
                sortedEquipments.comparatorProperty().bind(equipmentTable.comparatorProperty());
                equipmentTable.setItems(sortedEquipments);
            }
        }

        if (rentSearchField != null) {
            FilteredList<Rental> filteredRentals = new FilteredList<>(rentalList, p -> true);
            rentSearchField.textProperty().addListener((obs, oldVal, newVal) ->
                    filteredRentals.setPredicate(r -> (r.getMachineName() != null && r.getMachineName().toLowerCase().contains(newVal.toLowerCase())) ||
                            (r.getPatientName() != null && r.getPatientName().toLowerCase().contains(newVal.toLowerCase())))
            );
            SortedList<Rental> sortedRentals = new SortedList<>(filteredRentals);
            if (rentTable != null) {
                sortedRentals.comparatorProperty().bind(rentTable.comparatorProperty());
                rentTable.setItems(sortedRentals);
            }
        }
    }

    // ------------------- RENT EQUIPMENT BUTTON -------------------
    @FXML
    private void onRentEquipmentBtnClick() {
        hideAllPages();
        rentEquipmentPage.setVisible(true);
    }

    // ------------------- LOGOUT -------------------
    private void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/equipment/rental/equipmentrentalsystem2025/login.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage) logoutBtn.getScene().getWindow();
            currentStage.close();

            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(root));
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private int getTotalRentals() {
        String sql = "SELECT COUNT(*) FROM rental";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getActiveRents() {
        String sql = "SELECT COUNT(*) FROM rental WHERE status = 'Active'";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getClosedRents() {
        String sql = "SELECT COUNT(*) FROM rental WHERE status = 'Closed'";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getDueWithinDays(int days) {
        String sql = " SELECT COUNT(*) FROM rental WHERE status = 'Active' AND rent_end_date BETWEEN DATE('now') AND DATE('now', ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "+" + days + " days");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private double getWeeklyCollection() {
        String sql = " SELECT SUM(e.rental_price) FROM rental r JOIN equipment e ON r.equipment_id = e.equipment_id WHERE r.rent_collection_date >= DATE('now', '-7 days')";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    @FXML
    private void showEquipmentAdd() {
        hideAllPages();
        equipmentAddPage.setVisible(true);
    }

    @FXML
    private void showEquipmentList() {
        hideAllPages();
        equipmentListPage.setVisible(true);
    }

    private void hideAllPages() {
        if (homePage != null) homePage.setVisible(false);
        if (equipmentAddPage != null) equipmentAddPage.setVisible(false);
        if (equipmentListPage != null) equipmentListPage.setVisible(false);
        if (rentEquipmentPage != null) rentEquipmentPage.setVisible(false);
        if (availableEquipmentPage != null) availableEquipmentPage.setVisible(false);
        if (employeePage != null) employeePage.setVisible(false);
        if (activeRentPage != null) activeRentPage.setVisible(false);
        if (closedRentPage != null) closedRentPage.setVisible(false);
        if (dueRentPage != null) dueRentPage.setVisible(false);
        if (weeklyCollectionPage != null) weeklyCollectionPage.setVisible(false);


    }

    public void refreshEquipmentData() {
        loadEquipmentData();
        updateDashboardStats();
        populateChart();
    }

    @FXML
    private void openEmployeePage() {
        if (employeePage == null) {
            System.out.println("employeePage is NULL – check fx:id in Dashboard.fxml");
            return;
        }
        hideAllPages();
        employeePage.setVisible(true);
    }

    @FXML
    private void showActiveRentPage() {
        hideAllPages();
        activeRentPage.setVisible(true);
        loadActiveRentData();
    }

    @FXML
    private void showClosedRentPage() {
        hideAllPages();
        closedRentPage.setVisible(true);
        loadClosedRentData();
    }

    @FXML
    private void showDueRentPage() {
        hideAllPages();
        dueRentPage.setVisible(true);
        loadDueRentData();
    }

    @FXML
    private void showWeeklyCollectionPage() {

        if (weeklyCollectionPage == null || weeklyCollectionTable == null) {
            System.out.println("❌ weeklyCollectionPage OR table is NULL – check fx:id");
            return;
        }

        hideAllPages();

        weeklyCollectionPage.setVisible(true);
        weeklyCollectionPage.setManaged(true);

        loadWeeklyCollectionData();
    }
    private boolean weeklyColumnsInitialized = false;

    private void initWeeklyTableColumns() {

        if (weeklyColumnsInitialized) return;

        TableColumn<WeeklyCollectionRow, String> dateCol =
                new TableColumn<>("Date");
        dateCol.setCellValueFactory(c -> c.getValue().dateProperty());
        dateCol.setPrefWidth(200);

        TableColumn<WeeklyCollectionRow, Number> amountCol =
                new TableColumn<>("Collected (₹)");
        amountCol.setCellValueFactory(c -> c.getValue().amountProperty());
        amountCol.setPrefWidth(200);

        weeklyCollectionTable.getColumns().addAll(dateCol, amountCol);

        weeklyColumnsInitialized = true;
    }

    private void loadWeeklyCollectionData() {

        initWeeklyTableColumns();

        ObservableList<WeeklyCollectionRow> data =
                FXCollections.observableArrayList();

        weeklyCollectionChart.getData().clear();
        XYChart.Series<String, Number> series =
                new XYChart.Series<>();
        series.setName("Weekly Collection");

        String sql =
                "SELECT r.rent_collection_date, " +
                        "SUM(e.rental_price) AS total " +
                        "FROM rental r " +
                        "JOIN equipment e ON r.equipment_id = e.equipment_id " +
                        "WHERE r.rent_collection_date >= DATE('now','-7 days') " +
                        "GROUP BY r.rent_collection_date " +
                        "ORDER BY r.rent_collection_date";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String date = rs.getString("rent_collection_date");
                double total = rs.getDouble("total");

                data.add(new WeeklyCollectionRow(date, total));
                series.getData().add(new XYChart.Data<>(date, total));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        weeklyCollectionTable.setItems(data);
        weeklyCollectionChart.getData().add(series);
    }



}
