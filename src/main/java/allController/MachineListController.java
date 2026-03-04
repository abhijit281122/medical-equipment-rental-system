package allController;

import dbUtils.Database;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.*;
import javafx.scene.control.*;
import model.MachineModel;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class MachineListController implements Initializable {

    @FXML private Label pageTitle;
    @FXML private TextField searchField;
    @FXML private TableView<MachineModel> machineTable;

    @FXML private TableColumn<MachineModel, Integer> colId;
    @FXML private TableColumn<MachineModel, String> colName;
    @FXML private TableColumn<MachineModel, String> colCategory;
    @FXML private TableColumn<MachineModel, String> colManufacturer;
    @FXML private TableColumn<MachineModel, Integer> colTotalUnits;
    @FXML private TableColumn<MachineModel, String> colStatus;

    private final ObservableList<MachineModel> machineList = FXCollections.observableArrayList();
    private Connection connection;
    private String filterType = "ALL";   // ALL / AVAILABLE

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        connection = Database.getInstance().getConnection();
        setupColumns();
        loadMachines();
        setupSearch();
    }

    private void setupColumns() {

        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());

        colName.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

        colCategory.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getCategory()));

        colManufacturer.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getManufacturer()));

        colTotalUnits.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getTotalUnits()).asObject());

        colStatus.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));
    }

    private void loadMachines() {

        machineList.clear();

        try {

            String sql =
                    "SELECT m.id, m.machine_name, m.category, m.manufacturer, " +
                            "COUNT(mu.id) AS total_units, mu.status " +
                            "FROM machine m " +
                            "LEFT JOIN machine_unit mu ON m.id = mu.machine_id AND mu.deleted = 0 ";

            if (filterType.equals("AVAILABLE")) {
                sql += "WHERE m.deleted = 0 AND mu.status = 'AVAILABLE' ";
            } else {
                sql += "WHERE m.deleted = 0 ";
            }

            sql += "GROUP BY m.id";

            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                machineList.add(new MachineModel(
                        rs.getInt("id"),
                        rs.getString("machine_name"),
                        rs.getString("category"),
                        rs.getString("manufacturer"),
                        rs.getInt("total_units"),
                        rs.getString("status")
                ));
            }

            machineTable.setItems(machineList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupSearch() {

        FilteredList<MachineModel> filtered = new FilteredList<>(machineList, b -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {

            filtered.setPredicate(machine -> {

                if (newVal == null || newVal.isEmpty()) return true;

                String keyword = newVal.toLowerCase();

                return machine.getName().toLowerCase().contains(keyword)
                        || machine.getCategory().toLowerCase().contains(keyword)
                        || machine.getManufacturer().toLowerCase().contains(keyword);
            });
        });

        machineTable.setItems(filtered);
    }

    public void setFilterType(String type) {
        this.filterType = type;
        pageTitle.setText(type + " Machines");
        loadMachines();
    }
}