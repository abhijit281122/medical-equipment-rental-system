package allModels;

import javafx.beans.property.*;

public class DueDetails {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty equipmentName = new SimpleStringProperty();
    private final StringProperty patientName = new SimpleStringProperty();
    private final StringProperty endDate = new SimpleStringProperty();
    private final IntegerProperty dueDays = new SimpleIntegerProperty();
    private final DoubleProperty dailyRent = new SimpleDoubleProperty();

    public DueDetails(int id, String equipmentName, String patientName, String endDate, int dueDays, double dailyRent) {
        this.id.set(id);
        this.equipmentName.set(equipmentName);
        this.patientName.set(patientName);
        this.endDate.set(endDate);
        this.dueDays.set(dueDays);
        this.dailyRent.set(dailyRent);
    }

    public IntegerProperty idProperty() { return id; }
    public StringProperty equipmentNameProperty() { return equipmentName; }
    public StringProperty patientNameProperty() { return patientName; }
    public StringProperty endDateProperty() { return endDate; }
    public IntegerProperty dueDaysProperty() { return dueDays; }
    public DoubleProperty dailyRentProperty() { return dailyRent; }

    public String getEquipmentName() { return equipmentName.get(); }
    public String getPatientName() { return patientName.get(); }
    public String getEndDate() { return endDate.get(); }
    public int getDueDays() { return dueDays.get(); }
    public double getDailyRent() { return dailyRent.get(); }
}
