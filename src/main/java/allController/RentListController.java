package allController;

import allModels.RentModel;
import dbUtils.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class RentListController implements Initializable {

    @FXML
    private Label pageTitle;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<RentModel> rentTable;

    @FXML
    private TableColumn<RentModel, String> challanCol;
    @FXML
    private TableColumn<RentModel, String> customerCol;
    @FXML
    private TableColumn<RentModel, String> machineCol;
    @FXML
    private TableColumn<RentModel, String> unitCol;
    @FXML
    private TableColumn<RentModel, String> startDateCol;
    @FXML
    private TableColumn<RentModel, String> returnDateCol;
    @FXML
    private TableColumn<RentModel, Integer> daysCol;
    @FXML
    private TableColumn<RentModel, Double> totalCol;
    @FXML
    private TableColumn<RentModel, Double> paidCol;
    @FXML
    private TableColumn<RentModel, Double> dueCol;
    @FXML
    private TableColumn<RentModel, String> statusCol;

    private final ObservableList<RentModel> rentList = FXCollections.observableArrayList();
    private Connection connection;

    private String rentType = "ALL";  // ACTIVE / CLOSED / DUE

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        connection = Database.getInstance().getConnection();

        setupColumns();
        loadRentData();
        setupSearch();
    }

    // ================= COLUMN BINDING =================
    private void setupColumns() {

        challanCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getChallanNo()));

        customerCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getCustomerName()));

        machineCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getMachineName()));

        unitCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getSerialNumber()));

        startDateCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getStartDate()));

        returnDateCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getExpectedReturnDate()));

        daysCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getDays()).asObject());

        totalCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(data.getValue().getTotalAmount()).asObject());

        paidCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPaidAmount()).asObject());

        dueCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(data.getValue().getDueAmount()).asObject());

        statusCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));
    }

    // ================= LOAD DATA =================
    private void loadRentData() {

        rentList.clear();

        try {

            String sql =
                    "SELECT r.id, r.challan_no, c.full_name, m.machine_name, mu.serial_number, " +
                            "r.rent_start_date, r.expected_return_date, r.days, " +
                            "r.total_amount, r.paid_amount, r.due_amount, r.status " +
                            "FROM rental r " +
                            "JOIN customer c ON r.customer_id = c.id " +
                            "JOIN machine_unit mu ON r.machine_unit_id = mu.id " +
                            "JOIN machine m ON mu.machine_id = m.id " +
                            "WHERE r.is_deleted = 0 ";

            if (rentType.equals("ACTIVE")) {
                sql += " AND r.status = 'ACTIVE' ";
            } else if (rentType.equals("CLOSED")) {
                sql += " AND r.status = 'CLOSED' ";
            } else if (rentType.equals("DUE")) {
                sql += " AND r.status = 'ACTIVE' AND r.expected_return_date < CURDATE() ";
            }

            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                rentList.add(new RentModel(
                        rs.getInt("id"),
                        rs.getString("challan_no"),
                        rs.getString("full_name"),
                        rs.getString("machine_name"),
                        rs.getString("serial_number"),
                        rs.getString("rent_start_date"),
                        rs.getString("expected_return_date"),
                        rs.getInt("days"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("paid_amount"),
                        rs.getDouble("due_amount"),
                        rs.getString("status")
                ));
            }

            rentTable.setItems(rentList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= SEARCH FILTER =================
    private void setupSearch() {

        FilteredList<RentModel> filteredData = new FilteredList<>(rentList, b -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {

            filteredData.setPredicate(rent -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String keyword = newValue.toLowerCase();

                return rent.getChallanNo().toLowerCase().contains(keyword)
                        || rent.getCustomerName().toLowerCase().contains(keyword)
                        || rent.getMachineName().toLowerCase().contains(keyword)
                        || rent.getStatus().toLowerCase().contains(keyword);
            });
        });

        rentTable.setItems(filteredData);
    }


    public void setRentType(String type) {
        this.rentType = type;
        pageTitle.setText(type + " Rents");
        loadRentData();
    }
}