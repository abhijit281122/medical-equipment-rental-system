package allModels;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Employee {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty employeeCode = new SimpleStringProperty();
    private final StringProperty fullName = new SimpleStringProperty();
    private final StringProperty phone = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty address = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> joiningDate = new SimpleObjectProperty<>();
    private final DoubleProperty salary = new SimpleDoubleProperty();
    private final StringProperty roleName = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();

    public Employee(int id, String code, String name, String phone,
                    String email, String address, LocalDate joiningDate,
                    double salary, String roleName, String status) {

        this.id.set(id);
        this.employeeCode.set(code);
        this.fullName.set(name);
        this.phone.set(phone);
        this.email.set(email);
        this.address.set(address);
        this.joiningDate.set(joiningDate);
        this.salary.set(salary);
        this.roleName.set(roleName);
        this.status.set(status);
    }

    public int getId() {
        return id.get();
    }

    public String getEmployeeCode() {
        return employeeCode.get();
    }

    public String getFullName() {
        return fullName.get();
    }

    public String getPhone() {
        return phone.get();
    }

    public String getEmail() {
        return email.get();
    }

    public String getAddress() {
        return address.get();
    }

    public LocalDate getJoiningDate() {
        return joiningDate.get();
    }

    public double getSalary() {
        return salary.get();
    }

    public String getRoleName() {
        return roleName.get();
    }

    public String getStatus() {
        return status.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty employeeCodeProperty() {
        return employeeCode;
    }

    public StringProperty fullNameProperty() {
        return fullName;
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public StringProperty addressProperty() {
        return address;
    }

    public ObjectProperty<LocalDate> joiningDateProperty() {
        return joiningDate;
    }

    public DoubleProperty salaryProperty() {
        return salary;
    }

    public StringProperty roleNameProperty() {
        return roleName;
    }

    public StringProperty statusProperty() {
        return status;
    }
}