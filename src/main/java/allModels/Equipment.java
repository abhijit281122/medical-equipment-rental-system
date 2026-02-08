package allModels;

import javafx.beans.property.*;

public class Equipment {

    private final StringProperty equipmentId = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty condition = new SimpleStringProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();       // Added status
    private final DoubleProperty rentalPrice = new SimpleDoubleProperty();   // Added rentalPrice

    public Equipment(String equipmentId, String name, String condition, String category) {
        this(equipmentId, name, condition, category, "Available", 0.0);
    }

    public Equipment(String equipmentId, String name, String condition, String category, String status, double rentalPrice) {
        this.equipmentId.set(equipmentId);
        this.name.set(name);
        this.condition.set(condition);
        this.category.set(category);
        this.status.set(status);
        this.rentalPrice.set(rentalPrice);
    }

    // Property getters
    public StringProperty equipmentIdProperty() { return equipmentId; }
    public StringProperty nameProperty() { return name; }
    public StringProperty conditionProperty() { return condition; }
    public StringProperty categoryProperty() { return category; }
    public StringProperty statusProperty() { return status; }
    public DoubleProperty rentalPriceProperty() { return rentalPrice; }

    // Value getters
    public String getEquipmentId() { return equipmentId.get(); }
    public String getName() { return name.get(); }
    public String getCondition() { return condition.get(); }
    public String getCategory() { return category.get(); }
    public String getStatus() { return status.get(); }
    public double getRentalPrice() { return rentalPrice.get(); }

    // Value setters (optional, in case you need to update status or price)
    public void setStatus(String status) { this.status.set(status); }
    public void setRentalPrice(double price) { this.rentalPrice.set(price); }
}
