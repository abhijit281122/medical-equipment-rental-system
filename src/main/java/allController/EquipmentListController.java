package allController;

import allModels.EquipmentData;
import dbUtils.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EquipmentListController {

    @FXML
    private TableView<EquipmentData> equipmentTable;
    @FXML
    private TableColumn<EquipmentData, String> colId;
    @FXML
    private TableColumn<EquipmentData, String> colName;
    @FXML
    private TableColumn<EquipmentData, String> colCategory;
    @FXML
    private TableColumn<EquipmentData, String> colManufacturer;
    @FXML
    private TableColumn<EquipmentData, String> colSerial;
    @FXML
    private TableColumn<EquipmentData, Double> colPrice;
    @FXML
    private TableColumn<EquipmentData, String> colCondition;

    public final ObservableList<EquipmentData> list = FXCollections.observableArrayList();
    private final Connection connection = Database.getInstance().getConnection();

    // Static instance to allow refresh from form controller
    public static EquipmentListController instance;

    public EquipmentListController() throws SQLException {
    }

    @FXML
    public void initialize() {
        instance = this;

        colId.setCellValueFactory(data -> data.getValue().equipmentIdProperty());
        colName.setCellValueFactory(data -> data.getValue().equipmentNameProperty());
        colCategory.setCellValueFactory(data -> data.getValue().categoryProperty());
        colManufacturer.setCellValueFactory(data -> data.getValue().manufacturerProperty());
        colSerial.setCellValueFactory(data -> data.getValue().serialNumberProperty());
        colPrice.setCellValueFactory(data -> data.getValue().rentalPriceProperty().asObject());
        colCondition.setCellValueFactory(data -> data.getValue().conditionProperty());

        loadEquipment();
    }

    public void loadEquipment() {
        list.clear();
        String sql = "SELECT * FROM equipment";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new EquipmentData(
                        rs.getString("equipment_id"),
                        rs.getString("equipment_name"),
                        rs.getString("category"),
                        rs.getString("manufacturer"),
                        rs.getString("serial_number"),
                        rs.getDouble("rental_price"),
                        rs.getString("condition")
                ));
            }

            equipmentTable.setItems(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
