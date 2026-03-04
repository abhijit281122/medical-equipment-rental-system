package model;

import javafx.beans.property.*;

public class MachineModel {

    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty category;
    private final StringProperty manufacturer;
    private final IntegerProperty totalUnits;
    private final StringProperty status;

    public MachineModel(int id, String name, String category,
                        String manufacturer, int totalUnits, String status) {

        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.category = new SimpleStringProperty(category);
        this.manufacturer = new SimpleStringProperty(manufacturer);
        this.totalUnits = new SimpleIntegerProperty(totalUnits);
        this.status = new SimpleStringProperty(status);
    }

    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getCategory() { return category.get(); }
    public String getManufacturer() { return manufacturer.get(); }
    public int getTotalUnits() { return totalUnits.get(); }
    public String getStatus() { return status.get(); }
}