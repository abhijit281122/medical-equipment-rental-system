package allModels;

import java.time.LocalDate;

public class Rental {

    private int id;
    private String challanNo;

    private int machineUnitId;
    private int customerId;

    private int createdBy;
    private int deliveryPersonId;
    private int rentCollectorId;

    private LocalDate rentStartDate;
    private LocalDate rentEndDate;
    private LocalDate actualReturnDate;

    private double price;
    private int days;

    private double totalAmount;
    private double paidAmount;
    private double dueAmount;

    private String status;
    private String remarks;

    private String createdAt;
    private int isDeleted;

    // ================= CONSTRUCTOR =================

    public Rental() {
    }

    // ================= GETTERS & SETTERS =================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getChallanNo() {
        return challanNo;
    }

    public void setChallanNo(String challanNo) {
        this.challanNo = challanNo;
    }


    public int getMachineUnitId() {
        return machineUnitId;
    }

    public void setMachineUnitId(int machineUnitId) {
        this.machineUnitId = machineUnitId;
    }


    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }


    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }


    public int getDeliveryPersonId() {
        return deliveryPersonId;
    }

    public void setDeliveryPersonId(int deliveryPersonId) {
        this.deliveryPersonId = deliveryPersonId;
    }


    public int getRentCollectorId() {
        return rentCollectorId;
    }

    public void setRentCollectorId(int rentCollectorId) {
        this.rentCollectorId = rentCollectorId;
    }


    public LocalDate getRentStartDate() {
        return rentStartDate;
    }

    public void setRentStartDate(LocalDate rentStartDate) {
        this.rentStartDate = rentStartDate;
    }


    public LocalDate getRentEndDate() {
        return rentEndDate;
    }

    public void setRentEndDate(LocalDate rentEndDate) {
        this.rentEndDate = rentEndDate;
    }


    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }


    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }


    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }


    public double getDueAmount() {
        return dueAmount;
    }

    public void setDueAmount(double dueAmount) {
        this.dueAmount = dueAmount;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
}