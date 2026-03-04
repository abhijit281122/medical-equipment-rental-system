package allModels;

import javafx.beans.property.*;

public class RentModel {

    private final IntegerProperty id;
    private final StringProperty challanNo;
    private final StringProperty customerName;
    private final StringProperty machineName;
    private final StringProperty serialNumber;
    private final StringProperty startDate;
    private final StringProperty expectedReturnDate;
    private final IntegerProperty days;
    private final DoubleProperty totalAmount;
    private final DoubleProperty paidAmount;
    private final DoubleProperty dueAmount;
    private final StringProperty status;

    public RentModel(int id,
                     String challanNo,
                     String customerName,
                     String machineName,
                     String serialNumber,
                     String startDate,
                     String expectedReturnDate,
                     int days,
                     double totalAmount,
                     double paidAmount,
                     double dueAmount,
                     String status) {

        this.id = new SimpleIntegerProperty(id);
        this.challanNo = new SimpleStringProperty(challanNo);
        this.customerName = new SimpleStringProperty(customerName);
        this.machineName = new SimpleStringProperty(machineName);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.startDate = new SimpleStringProperty(startDate);
        this.expectedReturnDate = new SimpleStringProperty(expectedReturnDate);
        this.days = new SimpleIntegerProperty(days);
        this.totalAmount = new SimpleDoubleProperty(totalAmount);
        this.paidAmount = new SimpleDoubleProperty(paidAmount);
        this.dueAmount = new SimpleDoubleProperty(dueAmount);
        this.status = new SimpleStringProperty(status);
    }

    public int getId() {
        return id.get();
    }

    public String getChallanNo() {
        return challanNo.get();
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public String getMachineName() {
        return machineName.get();
    }

    public String getSerialNumber() {
        return serialNumber.get();
    }

    public String getStartDate() {
        return startDate.get();
    }

    public String getExpectedReturnDate() {
        return expectedReturnDate.get();
    }

    public int getDays() {
        return days.get();
    }

    public double getTotalAmount() {
        return totalAmount.get();
    }

    public double getPaidAmount() {
        return paidAmount.get();
    }

    public double getDueAmount() {
        return dueAmount.get();
    }

    public String getStatus() {
        return status.get();
    }
}