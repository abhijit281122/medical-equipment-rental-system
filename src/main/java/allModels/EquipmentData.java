package allModels;

import javafx.beans.property.*;

public class EquipmentData {

    private final StringProperty equipmentId = new SimpleStringProperty();
    private final StringProperty equipmentName = new SimpleStringProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final StringProperty manufacturer = new SimpleStringProperty();
    private final StringProperty serialNumber = new SimpleStringProperty();
    private final DoubleProperty rentalPrice = new SimpleDoubleProperty();
    private final StringProperty condition = new SimpleStringProperty();

    public EquipmentData(String id, String name, String category,
                         String manufacturer, String serial,
                         double price, String condition) {
        this.equipmentId.set(id);
        this.equipmentName.set(name);
        this.category.set(category);
        this.manufacturer.set(manufacturer);
        this.serialNumber.set(serial);
        this.rentalPrice.set(price);
        this.condition.set(condition);
    }

    public StringProperty equipmentIdProperty() { return equipmentId; }
    public StringProperty equipmentNameProperty() { return equipmentName; }
    public StringProperty categoryProperty() { return category; }
    public StringProperty manufacturerProperty() { return manufacturer; }
    public StringProperty serialNumberProperty() { return serialNumber; }
    public DoubleProperty rentalPriceProperty() { return rentalPrice; }
    public StringProperty conditionProperty() { return condition; }
}
