package allController;

import dbUtils.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    // ================= TOP BAR =================
    @FXML
    private Label usernameLabel;
    @FXML
    private Button logoutBtn;

    // ================= CENTER CONTENT =================
    @FXML
    private StackPane contentArea;
    @FXML
    private VBox homePage;

    // ================= DASHBOARD LABELS =================
    @FXML
    private Label totalMachineLabel;
    @FXML
    private Label availableMachinesLabel;
    @FXML
    private Label totalRentalsLabel;
    @FXML
    private Label activeRentsLabel;
    @FXML
    private Label closedRentsLabel;
    @FXML
    private Label dueWithinPeriodLabel;
    @FXML
    private Label dateWiseDueLabel;
    @FXML
    private Label weeklyCollectionLabel;


    @FXML
    private BarChart<String, Number> machineChart;

    private Connection connection;

    // ================= INITIALIZE =================
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        connection = Database.getInstance().getConnection();

        openHomePage();

        loadMachineStats();
        loadRentalStats();
        loadDueStats();
        loadWeeklyCollection();
        loadMachineChart();

        if (logoutBtn != null) {
            logoutBtn.setOnAction(e -> logout());
        }
    }

    // ================= DASHBOARD LOGIC =================

    private void loadMachineStats() {
        try (Statement stmt = connection.createStatement()) {

            ResultSet rs1 = stmt.executeQuery(
                    "SELECT COUNT(*) FROM machine_unit WHERE deleted=0"
            );
            if (rs1.next()) {
                totalMachineLabel.setText(String.valueOf(rs1.getInt(1)));
            }

            ResultSet rs2 = stmt.executeQuery(
                    "SELECT COUNT(*) FROM machine_unit WHERE status='Available' AND deleted=0"
            );
            if (rs2.next()) {
                availableMachinesLabel.setText(String.valueOf(rs2.getInt(1)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRentalStats() {
        try (Statement stmt = connection.createStatement()) {

            ResultSet total = stmt.executeQuery(
                    "SELECT COUNT(*) FROM rental WHERE is_deleted=0"
            );
            if (total.next()) {
                totalRentalsLabel.setText(String.valueOf(total.getInt(1)));
            }

            ResultSet active = stmt.executeQuery(
                    "SELECT COUNT(*) FROM rental WHERE status='Active' AND is_deleted=0"
            );
            if (active.next()) {
                activeRentsLabel.setText(String.valueOf(active.getInt(1)));
            }

            ResultSet closed = stmt.executeQuery(
                    "SELECT COUNT(*) FROM rental WHERE status='Returned' AND is_deleted=0"
            );
            if (closed.next()) {
                closedRentsLabel.setText(String.valueOf(closed.getInt(1)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDueStats() {
        try (Statement stmt = connection.createStatement()) {

            ResultSet rs = stmt.executeQuery(
                    "SELECT COUNT(*) FROM rental " +
                            "WHERE due_amount > 0 " +
                            "AND expected_return_date <= date('now','+7 day') " +
                            "AND status='Active' " +
                            "AND is_deleted=0"
            );

            if (rs.next()) {
                dueWithinPeriodLabel.setText(String.valueOf(rs.getInt(1)));
            }

            ResultSet today = stmt.executeQuery(
                    "SELECT COUNT(*) FROM rental " +
                            "WHERE expected_return_date=date('now') " +
                            "AND status='Active' AND is_deleted=0"
            );

            if (today.next()) {
                dateWiseDueLabel.setText(String.valueOf(today.getInt(1)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadWeeklyCollection() {
        try (Statement stmt = connection.createStatement()) {

            ResultSet rs = stmt.executeQuery(
                    "SELECT IFNULL(SUM(paid_amount),0) FROM rental " +
                            "WHERE created_at >= date('now','-7 day') " +
                            "AND is_deleted=0"
            );

            if (rs.next()) {
                weeklyCollectionLabel.setText("₹ " + rs.getDouble(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMachineChart() {

        if (machineChart == null) return;

        machineChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Units");

        try (Statement stmt = connection.createStatement()) {

            String sql =
                    "SELECT m.machine_name, COUNT(mu.id) AS total " +
                            "FROM machine m " +
                            "LEFT JOIN machine_unit mu ON m.id = mu.machine_id " +
                            "AND mu.deleted=0 " +
                            "GROUP BY m.machine_name";

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                series.getData().add(
                        new XYChart.Data<>(
                                rs.getString("machine_name"),
                                rs.getInt("total")
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        machineChart.getData().add(series);
    }

    // ================= LOAD EXTERNAL PAGE =================
    private void loadPage(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource(
                            "/com/equipment/rental/equipmentrentalsystem2025/" + fxmlFile
                    ))
            );

            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= HOME =================
    @FXML
    private void openHomePage() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(homePage);
    }

    // ================= SIDEBAR =================
    @FXML
    private void openMachineMasterPage() {
        loadPage("MachineMaster.fxml");
    }

    @FXML
    private void openMachineUnitPage() {
        loadPage("MachineUnit.fxml");
    }

    @FXML
    private void openRentMachinePage() {
        loadPage("RentMachinePage.fxml");
    }

    @FXML
    private void openEmployeePage() {
        loadPage("Employee.fxml");
    }

    @FXML
    private void openEmployeeRolePage() {
        loadPage("EmployeeRole.fxml");
    }

    @FXML
    private void openCustomerPage() {
        loadPage("Customer.fxml");
    }

    // ================= REPORT BUTTONS =================
    @FXML
    private void showActiveRentPage() {
        System.out.println("Active Rent clicked");
    }

    @FXML
    private void showClosedRentPage() {
        System.out.println("Closed Rent clicked");
    }

    @FXML
    private void showDueRentPage() {
        System.out.println("Due Rent clicked");
    }

    @FXML
    private void showWeeklyCollectionPage() {
        System.out.println("Weekly Collection clicked");
    }

    // ================= LOGOUT =================
    private void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/equipment/rental/equipmentrentalsystem2025/login.fxml"
                    )
            );

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

    @FXML
    private void openTotalMachines() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/equipment/rental/equipmentrentalsystem2025/MachineList.fxml")
            );

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Total Machines");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openAvailableMachines() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/equipment/rental/equipmentrentalsystem2025/MachineList.fxml")
            );

            Parent root = loader.load();

            MachineListController controller = loader.getController();
            controller.setFilterType("AVAILABLE");

            Stage stage = new Stage();
            stage.setTitle("Available Machines");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openActiveRents() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/equipment/rental/equipmentrentalsystem2025/RentListPage.fxml")
            );

            Parent root = loader.load();

            RentListController controller = loader.getController();
            controller.setRentType("ACTIVE");

            Stage stage = new Stage();
            stage.setTitle("Active Rents");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openClosedRents() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/equipment/rental/equipmentrentalsystem2025/RentListPage.fxml")
            );

            Parent root = loader.load();

            RentListController controller = loader.getController();
            controller.setRentType("CLOSED");

            Stage stage = new Stage();
            stage.setTitle("Closed Rents");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openDueRents() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/equipment/rental/equipmentrentalsystem2025/RentListPage.fxml")
            );

            Parent root = loader.load();

            RentListController controller = loader.getController();
            controller.setRentType("DUE");

            Stage stage = new Stage();
            stage.setTitle("Due Rents");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}