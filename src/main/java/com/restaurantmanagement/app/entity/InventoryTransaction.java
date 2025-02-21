package com.restaurantmanagement.app.entity;

import javafx.beans.property.*;

public class InventoryTransaction {

    private final IntegerProperty transactionID;
    private final IntegerProperty transactionTypeID;
    private final StringProperty supplierName;
    private final IntegerProperty ingredientID;
    private final DoubleProperty quantity;
    private final StringProperty unit;
    private final DoubleProperty price;
    private final StringProperty note;
    private final StringProperty transactionDate;

    public InventoryTransaction(int transactionID, int transactionTypeID, String supplierName,
                                int ingredientID, double quantity, String unit, double price,
                                String note, String transactionDate) {
        this.transactionID = new SimpleIntegerProperty(transactionID);
        this.transactionTypeID = new SimpleIntegerProperty(transactionTypeID);
        this.supplierName = new SimpleStringProperty(supplierName);
        this.ingredientID = new SimpleIntegerProperty(ingredientID);
        this.quantity = new SimpleDoubleProperty(quantity);
        this.unit = new SimpleStringProperty(unit);
        this.price = new SimpleDoubleProperty(price);
        this.note = new SimpleStringProperty(note);
        this.transactionDate = new SimpleStringProperty(transactionDate);
    }

    // Getter and Setter methods
    public int getTransactionID() {
        return transactionID.get();
    }

    public void setTransactionID(int transactionID) {
        this.transactionID.set(transactionID);
    }

    public IntegerProperty transactionIDProperty() {
        return transactionID;
    }

    public int getTransactionTypeID() {
        return transactionTypeID.get();
    }

    public void setTransactionTypeID(int transactionTypeID) {
        this.transactionTypeID.set(transactionTypeID);
    }

    public IntegerProperty transactionTypeIDProperty() {
        return transactionTypeID;
    }

    public String getSupplierName() {
        return supplierName.get();
    }

    public void setSupplierName(String supplierName) {
        this.supplierName.set(supplierName);
    }

    public StringProperty supplierNameProperty() {
        return supplierName;
    }

    public int getIngredientID() {
        return ingredientID.get();
    }

    public void setIngredientID(int ingredientID) {
        this.ingredientID.set(ingredientID);
    }

    public IntegerProperty ingredientIDProperty() {
        return ingredientID;
    }

    public double getQuantity() {
        return quantity.get();
    }

    public void setQuantity(double quantity) {
        this.quantity.set(quantity);
    }

    public DoubleProperty quantityProperty() {
        return quantity;
    }

    public String getUnit() {
        return unit.get();
    }

    public void setUnit(String unit) {
        this.unit.set(unit);
    }

    public StringProperty unitProperty() {
        return unit;
    }

    public double getPrice() {
        return price.get();
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public String getNote() {
        return note.get();
    }

    public void setNote(String note) {
        this.note.set(note);
    }

    public StringProperty noteProperty() {
        return note;
    }

    public String getTransactionDate() {
        return transactionDate.get();
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate.set(transactionDate);
    }

    public StringProperty transactionDateProperty() {
        return transactionDate;
    }
}
