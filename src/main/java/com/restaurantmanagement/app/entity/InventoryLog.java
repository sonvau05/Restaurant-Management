package com.restaurantmanagement.app.entity;

import javafx.beans.property.*;

public class InventoryLog {
    private final StringProperty ingredient;
    private final StringProperty transactionType;
    private final FloatProperty quantity;
    private final StringProperty unit;
    private final DoubleProperty price;
    private final StringProperty note;
    private final StringProperty logDate;

    public InventoryLog(String ingredient, String transactionType, float quantity, String unit, double price, String note, String logDate) {
        this.ingredient = new SimpleStringProperty(ingredient);
        this.transactionType = new SimpleStringProperty(transactionType);
        this.quantity = new SimpleFloatProperty(quantity);
        this.unit = new SimpleStringProperty(unit);
        this.price = new SimpleDoubleProperty(price);
        this.note = new SimpleStringProperty(note);
        this.logDate = new SimpleStringProperty(logDate);
    }

    public StringProperty ingredientProperty() { return ingredient; }
    public StringProperty transactionTypeProperty() { return transactionType; }
    public FloatProperty quantityProperty() { return quantity; }
    public StringProperty unitProperty() {return unit;}
    public DoubleProperty priceProperty() { return price; }
    public StringProperty noteProperty() { return note; }
    public StringProperty logDateProperty() { return logDate; }
}
