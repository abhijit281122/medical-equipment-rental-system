package allModels;

import javafx.beans.property.*;

public class DateWiseDue {
    private final StringProperty equipmentName = new SimpleStringProperty();
    private final StringProperty patientName = new SimpleStringProperty();
    private final StringProperty endDate = new SimpleStringProperty();
    private final IntegerProperty dueDays = new SimpleIntegerProperty();
    private final DoubleProperty finalDue = new SimpleDoubleProperty();

    public DateWiseDue(String equipmentName, String patientName, String endDate, int dueDays, double finalDue) {
        this.equipmentName.set(equipmentName);
        this.patientName.set(patientName);
        this.endDate.set(endDate);
        this.dueDays.set(dueDays);
        this.finalDue.set(finalDue);
    }

    public StringProperty equipmentNameProperty() { return equipmentName; }
    public StringProperty patientNameProperty() { return patientName; }
    public StringProperty endDateProperty() { return endDate; }
    public IntegerProperty dueDaysProperty() { return dueDays; }
    public DoubleProperty finalDueProperty() { return finalDue; }

    public String getEquipmentName() { return equipmentName.get(); }
    public String getPatientName() { return patientName.get(); }
    public String getEndDate() { return endDate.get(); }
    public int getDueDays() { return dueDays.get(); }
    public double getFinalDue() { return finalDue.get(); }
}
