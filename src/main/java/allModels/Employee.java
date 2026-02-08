package allModels;

import javafx.beans.property.*;

public class Employee {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty employeeCode = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty role = new SimpleStringProperty();
    private final StringProperty phone = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty address = new SimpleStringProperty();
    private final BooleanProperty active = new SimpleBooleanProperty(true);

    // ---------- CONSTRUCTOR ----------
    public Employee(int id,
                    String employeeCode,
                    String name,
                    String role,
                    String phone,
                    String email,
                    String address,
                    boolean active) {

        this.id.set(id);
        this.employeeCode.set(employeeCode);
        this.name.set(name);
        this.role.set(role);
        this.phone.set(phone);
        this.email.set(email);
        this.address.set(address);
        this.active.set(active);
    }

    // ---------- ID ----------
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    // ---------- EMPLOYEE CODE ----------
    public String getEmployeeCode() {
        return employeeCode.get();
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode.set(employeeCode);
    }

    public StringProperty employeeCodeProperty() {
        return employeeCode;
    }

    // ---------- NAME ----------
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    // ---------- ROLE ----------
    public String getRole() {
        return role.get();
    }

    public void setRole(String role) {
        this.role.set(role);
    }

    public StringProperty roleProperty() {
        return role;
    }

    // ---------- PHONE ----------
    public String getPhone() {
        return phone.get();
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    // ---------- EMAIL ----------
    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    // ---------- ADDRESS ----------
    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public StringProperty addressProperty() {
        return address;
    }

    // ---------- ACTIVE (SOFT DELETE) ----------
    public boolean isActive() {
        return active.get();
    }

    public void setActive(boolean active) {
        this.active.set(active);
    }

    public BooleanProperty activeProperty() {
        return active;
    }
}
