package allModels;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Customer {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty fullName = new SimpleStringProperty();
    private final StringProperty phone = new SimpleStringProperty();
    private final StringProperty address = new SimpleStringProperty();
    private final StringProperty location = new SimpleStringProperty();
    private final StringProperty referenceBy = new SimpleStringProperty();

    public Customer(int id, String fullName, String phone,
                    String address, String location, String referenceBy) {

        this.id.set(id);
        this.fullName.set(fullName);
        this.phone.set(phone);
        this.address.set(address);
        this.location.set(location);
        this.referenceBy.set(referenceBy);
    }

    public int getId() {
        return id.get();
    }

    public String getFullName() {
        return fullName.get();
    }

    public String getPhone() {
        return phone.get();
    }

    public String getAddress() {
        return address.get();
    }

    public String getLocation() {
        return location.get();
    }

    public String getReferenceBy() {
        return referenceBy.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty fullNameProperty() {
        return fullName;
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public StringProperty addressProperty() {
        return address;
    }

    public StringProperty locationProperty() {
        return location;
    }

    public StringProperty referenceByProperty() {
        return referenceBy;
    }
}
