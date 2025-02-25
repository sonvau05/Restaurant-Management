package com.restaurantmanagement.app.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class Order {
    private int orderID;
    private BigDecimal totalAmount;
    private Timestamp orderDate;
    private String status;
    private List<OrderDetail> orderDetails;

    public Order(int orderID, BigDecimal totalAmount, Timestamp orderDate, String status, List<OrderDetail> orderDetails) {
        this.orderID = orderID;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.status = status;
        this.orderDetails = orderDetails;
    }

    public int getOrderID() { return orderID; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public Timestamp getOrderDate() { return orderDate; }
    public String getStatus() { return status; }
    public List<OrderDetail> getOrderDetails() { return orderDetails; }

    public String getItemsString() {
        StringBuilder items = new StringBuilder();
        for (OrderDetail detail : orderDetails) {
            items.append(detail.getItemName()).append(" (").append(detail.getCategoryName()).append(") x").append(detail.getQuantity()).append(", ");
        }
        return items.length() > 0 ? items.substring(0, items.length() - 2) : "";
    }
}