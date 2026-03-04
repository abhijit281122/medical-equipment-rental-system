package allModels;

import javafx.beans.property.*;

import java.time.LocalDate;

public class MachineUnit {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty machineId = new SimpleIntegerProperty();
    private final StringProperty machineName = new SimpleStringProperty(); // For table display
    private final StringProperty serialNumber = new SimpleStringProperty();
    private final StringProperty lotNumber = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty condition = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> purchaseDate = new SimpleObjectProperty<>();

    public MachineUnit(int id,
                       int machineId,
                       String machineName,
                       String serialNumber,
                       String lotNumber,
                       String status,
                       String condition,
                       LocalDate purchaseDate) {

        this.id.set(id);
        this.machineId.set(machineId);
        this.machineName.set(machineName);
        this.serialNumber.set(serialNumber);
        this.lotNumber.set(lotNumber);
        this.status.set(status);
        this.condition.set(condition);
        this.purchaseDate.set(purchaseDate);
    }

    // Properties
    public IntegerProperty idProperty() { return id; }
    public IntegerProperty machineIdProperty() { return machineId; }
    public StringProperty machineNameProperty() { return machineName; }
    public StringProperty serialNumberProperty() { return serialNumber; }
    public StringProperty lotNumberProperty() { return lotNumber; }
    public StringProperty statusProperty() { return status; }
    public StringProperty conditionProperty() { return condition; }
    public ObjectProperty<LocalDate> purchaseDateProperty() { return purchaseDate; }

    // Getters
    public int getId() { return id.get(); }
    public int getMachineId() { return machineId.get(); }
    public String getMachineName() { return machineName.get(); }
    public String getSerialNumber() { return serialNumber.get(); }
    public String getLotNumber() { return lotNumber.get(); }
    public String getStatus() { return status.get(); }
    public String getCondition() { return condition.get(); }
    public LocalDate getPurchaseDate() { return purchaseDate.get(); }

    // Setters
    public void setStatus(String value) { status.set(value); }
    public void setCondition(String value) { condition.set(value); }
}
