package allModels;

import javafx.beans.property.*;


public class WeeklyCollectionRow {

    private final StringProperty date = new SimpleStringProperty();
    private final DoubleProperty amount = new SimpleDoubleProperty();

    public WeeklyCollectionRow(String date, double amount) {
        this.date.set(date);
        this.amount.set(amount);
    }

    public StringProperty dateProperty() { return date; }
    public DoubleProperty amountProperty() { return amount; }

    public String getDate() { return date.get(); }
    public double getAmount() { return amount.get(); }
}


