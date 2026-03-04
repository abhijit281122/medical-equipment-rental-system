package allModels;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EmployeeRole {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty roleName = new SimpleStringProperty();

    public EmployeeRole(int id, String roleName) {
        this.id.set(id);
        this.roleName.set(roleName);
    }

    public int getId() {
        return id.get();
    }

    public String getRoleName() {
        return roleName.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty roleNameProperty() {
        return roleName;
    }
}
