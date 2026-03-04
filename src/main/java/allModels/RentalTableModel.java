package allModels;

import javafx.beans.property.*;

import java.time.LocalDate;

public class RentalTableModel {

    private final IntegerProperty serialNo;
    private final StringProperty machineInfo;
    private final DoubleProperty totalAmount;
    private final StringProperty customerName;
    private final ObjectProperty<LocalDate> rentStartDate;
    private final ObjectProperty<LocalDate> rentEndDate;

    public RentalTableModel(int serialNo,
                            String machineInfo,
                            double totalAmount,
                            String customerName,
                            LocalDate rentStartDate,
                            LocalDate rentEndDate) {

        this.serialNo = new SimpleIntegerProperty(serialNo);
        this.machineInfo = new SimpleStringProperty(machineInfo);
        this.totalAmount = new SimpleDoubleProperty(totalAmount);
        this.customerName = new SimpleStringProperty(customerName);
        this.rentStartDate = new SimpleObjectProperty<>(rentStartDate);
        this.rentEndDate = new SimpleObjectProperty<>(rentEndDate);
    }

    // ================= PROPERTIES =================

    public int getSerialNo() {
        return serialNo.get();
    }

    public IntegerProperty serialNoProperty() {
        return serialNo;
    }

    public String getMachineInfo() {
        return machineInfo.get();
    }

    public StringProperty machineInfoProperty() {
        return machineInfo;
    }

    public double getTotalAmount() {
        return totalAmount.get();
    }

    public DoubleProperty totalAmountProperty() {
        return totalAmount;
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public StringProperty customerNameProperty() {
        return customerName;
    }

    public LocalDate getRentStartDate() {
        return rentStartDate.get();
    }

    public ObjectProperty<LocalDate> rentStartDateProperty() {
        return rentStartDate;
    }

    public LocalDate getRentEndDate() {
        return rentEndDate.get();
    }

    public ObjectProperty<LocalDate> rentEndDateProperty() {
        return rentEndDate;
    }
}
