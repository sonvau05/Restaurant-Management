package com.restaurantmanagement.app.entity;

import javafx.beans.property.*;
import java.sql.Timestamp;
import java.math.BigDecimal;

public class Order {
    private final IntegerProperty orderID;
    private final ObjectProperty<BigDecimal> totalAmount;
    private final ObjectProperty<Timestamp> orderDate;
    private final StringProperty status;

    public Order(int orderID, BigDecimal totalAmount, Timestamp orderDate, String status) {
        this.orderID = new SimpleIntegerProperty(orderID);
        this.totalAmount = new SimpleObjectProperty<>(totalAmount);
        this.orderDate = new SimpleObjectProperty<>(orderDate);
        this.status = new SimpleStringProperty(status);
    }

    public int getOrderID() {
        return orderID.get();
    }
    public void setOrderID(int orderID) {
        this.orderID.set(orderID);
    }
    public IntegerProperty orderIDProperty() {
        return orderID;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount.get();
    }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount.set(totalAmount);
    }
    public ObjectProperty<BigDecimal> totalAmountProperty() {
        return totalAmount;
    }

    public Timestamp getOrderDate() {
        return orderDate.get();
    }
    public void setOrderDate(Timestamp orderDate) {
        this.orderDate.set(orderDate);
    }
    public ObjectProperty<Timestamp> orderDateProperty() {
        return orderDate;
    }

    public String getStatus() {
        return status.get();
    }
    public void setStatus(String status) {
        this.status.set(status);
    }
    public StringProperty statusProperty() {
        return status;
    }
}
