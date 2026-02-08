package allModels;

import javafx.beans.property.*;

public class Rental {

    private final IntegerProperty id = new SimpleIntegerProperty();

    private final StringProperty equipmentId = new SimpleStringProperty();
    private final StringProperty machineName = new SimpleStringProperty();

    private final StringProperty patientName = new SimpleStringProperty();
    private final StringProperty challanNo = new SimpleStringProperty();
    private final StringProperty staffSeparation = new SimpleStringProperty();

    private final StringProperty rentReceivedDate = new SimpleStringProperty();
    private final StringProperty rentCollectionDate = new SimpleStringProperty();
    private final StringProperty rentEndDate = new SimpleStringProperty();

    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty collectedBy = new SimpleStringProperty();

    // Amounts
    private final DoubleProperty amount = new SimpleDoubleProperty();      // price of this rental
    private final DoubleProperty totalAmount = new SimpleDoubleProperty(); // same as amount if single rental
    private final DoubleProperty paidAmount = new SimpleDoubleProperty();
    private final DoubleProperty dueAmount = new SimpleDoubleProperty();

    // Calculated (not stored)
    private final StringProperty period = new SimpleStringProperty();

    public Rental(int id,
                  String equipmentId,
                  String machineName,
                  String patientName,
                  String challanNo,
                  String staffSeparation,
                  String rentReceivedDate,
                  String rentCollectionDate,
                  String rentEndDate,
                  String status,
                  String collectedBy,
                  double amount,
                  double totalAmount,
                  double paidAmount,
                  double dueAmount,
                  String period) {

        this.id.set(id);
        this.equipmentId.set(equipmentId);
        this.machineName.set(machineName);

        this.patientName.set(patientName);
        this.challanNo.set(challanNo);
        this.staffSeparation.set(staffSeparation);

        this.rentReceivedDate.set(rentReceivedDate);
        this.rentCollectionDate.set(rentCollectionDate);
        this.rentEndDate.set(rentEndDate);

        this.status.set(status);
        this.collectedBy.set(collectedBy);

        this.amount.set(amount);
        this.totalAmount.set(totalAmount);
        this.paidAmount.set(paidAmount);
        this.dueAmount.set(dueAmount);

        this.period.set(period);
    }

    // Property getters
    public IntegerProperty idProperty() { return id; }
    public StringProperty equipmentIdProperty() { return equipmentId; }
    public StringProperty machineNameProperty() { return machineName; }
    public StringProperty patientNameProperty() { return patientName; }
    public StringProperty challanNoProperty() { return challanNo; }
    public StringProperty staffSeparationProperty() { return staffSeparation; }
    public StringProperty rentReceivedDateProperty() { return rentReceivedDate; }
    public StringProperty rentCollectionDateProperty() { return rentCollectionDate; }
    public StringProperty rentEndDateProperty() { return rentEndDate; }
    public StringProperty statusProperty() { return status; }
    public StringProperty collectedByProperty() { return collectedBy; }
    public DoubleProperty amountProperty() { return amount; }
    public DoubleProperty totalAmountProperty() { return totalAmount; }
    public DoubleProperty paidAmountProperty() { return paidAmount; }
    public DoubleProperty dueAmountProperty() { return dueAmount; }
    public StringProperty periodProperty() { return period; }

    // Getters
    public int getId() { return id.get(); }
    public String getEquipmentId() { return equipmentId.get(); }
    public String getMachineName() { return machineName.get(); }
    public String getPatientName() { return patientName.get(); }
    public String getChallanNo() { return challanNo.get(); }
    public String getStaffSeparation() { return staffSeparation.get(); }
    public String getRentReceivedDate() { return rentReceivedDate.get(); }
    public String getRentCollectionDate() { return rentCollectionDate.get(); }
    public String getRentEndDate() { return rentEndDate.get(); }
    public String getStatus() { return status.get(); }
    public String getCollectedBy() { return collectedBy.get(); }
    public double getAmount() { return amount.get(); }
    public double getTotalAmount() { return totalAmount.get(); }
    public double getPaidAmount() { return paidAmount.get(); }
    public double getDueAmount() { return dueAmount.get(); }
    public String getPeriod() { return period.get(); }
}
