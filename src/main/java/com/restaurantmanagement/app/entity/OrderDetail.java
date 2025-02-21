package com.restaurantmanagement.app.entity;

import javafx.beans.property.*;
import java.math.BigDecimal;

public class OrderDetail {
    private final IntegerProperty orderDetailID;
    private final IntegerProperty orderID;
    private final IntegerProperty itemID;
    private final IntegerProperty quantity;
    private final ObjectProperty<BigDecimal> price;

    public OrderDetail(int orderDetailID, int orderID, int itemID, int quantity, BigDecimal price) {
        this.orderDetailID = new SimpleIntegerProperty(orderDetailID);
        this.orderID = new SimpleIntegerProperty(orderID);
        this.itemID = new SimpleIntegerProperty(itemID);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleObjectProperty<>(price);
    }

    public int getOrderDetailID() {
        return orderDetailID.get();
    }
    public void setOrderDetailID(int orderDetailID) {
        this.orderDetailID.set(orderDetailID);
    }
    public IntegerProperty orderDetailIDProperty() {
        return orderDetailID;
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

    public int getItemID() {
        return itemID.get();
    }
    public void setItemID(int itemID) {
        this.itemID.set(itemID);
    }
    public IntegerProperty itemIDProperty() {
        return itemID;
    }

    public int getQuantity() {
        return quantity.get();
    }
    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }
    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price.get();
    }
    public void setPrice(BigDecimal price) {
        this.price.set(price);
    }
    public ObjectProperty<BigDecimal> priceProperty() {
        return price;
    }
}
