package allModels;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Machine {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty machineCode = new SimpleStringProperty();
    private final StringProperty machineName = new SimpleStringProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final StringProperty manufacturer = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();

    // ================= CONSTRUCTOR =================
    public Machine(int id,
                   String machineCode,
                   String machineName,
                   String category,
                   String manufacturer,
                   String description) {

        this.id.set(id);
        this.machineCode.set(machineCode);
        this.machineName.set(machineName);
        this.category.set(category);
        this.manufacturer.set(manufacturer);
        this.description.set(description);
    }

    // ================= PROPERTIES =================
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty machineCodeProperty() {
        return machineCode;
    }

    public StringProperty machineNameProperty() {
        return machineName;
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public StringProperty manufacturerProperty() {
        return manufacturer;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    // ================= GETTERS =================
    public int getId() {
        return id.get();
    }

    public String getMachineCode() {
        return machineCode.get();
    }

    public String getMachineName() {
        return machineName.get();
    }

    public String getCategory() {
        return category.get();
    }

    public String getManufacturer() {
        return manufacturer.get();
    }


    public String getDescription() {
        return description.get();
    }

    // ================= SETTERS =================
    public void setMachineCode(String value) {
        machineCode.set(value);
    }

    public void setMachineName(String value) {
        machineName.set(value);
    }

    public void setCategory(String value) {
        category.set(value);
    }

    public void setManufacturer(String value) {
        manufacturer.set(value);
    }


    public void setDescription(String value) {
        description.set(value);
    }
}