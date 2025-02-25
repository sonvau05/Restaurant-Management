package com.restaurantmanagement.app.entity;

import java.math.BigDecimal;

public class OrderDetail {
    private int orderDetailID;
    private int orderID;
    private int itemID;
    private String itemName;
    private String categoryName;
    private int quantity;
    private BigDecimal price;

    public OrderDetail(int orderDetailID, int orderID, int itemID, String itemName, String categoryName, int quantity, BigDecimal price) {
        this.orderDetailID = orderDetailID;
        this.orderID = orderID;
        this.itemID = itemID;
        this.itemName = itemName;
        this.categoryName = categoryName;
        this.quantity = quantity;
        this.price = price;
    }

    public int getOrderDetailID() { return orderDetailID; }
    public int getOrderID() { return orderID; }
    public int getItemID() { return itemID; }
    public String getItemName() { return itemName; }
    public String getCategoryName() { return categoryName; }
    public int getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
}