package allController;

import dbUtils.Database;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EquipmentFormController {

    @FXML
    private TextField equipmentIdField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField categoryField;
    @FXML
    private ComboBox<String> conditionBox;
    @FXML
    private TextField manufacturerField;
    @FXML
    private TextField serialNumberField;
    @FXML
    private TextField priceField;
    @FXML
    private Button saveBtn;

    private final Connection connection = Database.getInstance().getConnection();

    private static final String DEFAULT_IMAGE = "/images/default-equipment.png";

    public EquipmentFormController() throws SQLException {
    }

    @FXML
    public void initialize() {
        conditionBox.getItems().addAll("Available", "Rented", "Maintenance");

        equipmentIdField.setText(generateEquipmentId());

        saveBtn.setOnAction(e -> saveEquipment());
    }

    private void saveEquipment() {
        if (!validateForm()) return;

        String sql = "INSERT INTO equipment " +
                "(equipment_id, equipment_name, category, manufacturer, serial_number, rental_price, condition, image, date_added) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, DATE('now'))";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, equipmentIdField.getText());
            ps.setString(2, nameField.getText());
            ps.setString(3, categoryField.getText());
            ps.setString(4, manufacturerField.getText());
            ps.setString(5, serialNumberField.getText());
            ps.setDouble(6, Double.parseDouble(priceField.getText()));
            ps.setString(7, conditionBox.getValue());
            ps.setString(8, DEFAULT_IMAGE);

            ps.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Equipment saved successfully!");

            // Refresh the equipment list immediately
            if (EquipmentListController.instance != null) {
                EquipmentListController.instance.loadEquipment();
            }

            clearForm();

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save equipment. Check console for details.");
        }
    }

    private String generateEquipmentId() {
        return "EQ-" + System.currentTimeMillis();
    }

    private boolean validateForm() {
        if (nameField.getText().isEmpty()
                || categoryField.getText().isEmpty()
                || conditionBox.getValue() == null
                || priceField.getText().isEmpty()) {

            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required fields");
            return false;
        }

        try {
            Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Rental price must be a number");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void clearForm() {
        nameField.clear();
        categoryField.clear();
        conditionBox.getSelectionModel().clearSelection();
        manufacturerField.clear();
        serialNumberField.clear();
        priceField.clear();

        equipmentIdField.setText(generateEquipmentId());
    }
}
