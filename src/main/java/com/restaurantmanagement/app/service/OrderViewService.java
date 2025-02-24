package com.restaurantmanagement.app.service;

import com.restaurantmanagement.app.entity.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderViewService {
    private final Connection connection;

    public OrderViewService(Connection connection) {
        this.connection = connection;
    }

    public List<Order> getOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT OrderID, TotalAmount, OrderDate, Status FROM Orders";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("OrderID"),
                        rs.getBigDecimal("TotalAmount"),
                        rs.getTimestamp("OrderDate"),
                        rs.getString("Status")
                ));
            }
        }
        return orders;
    }

    public boolean updateOrderStatus(int orderId, String status) throws SQLException {
        String query = "UPDATE Orders SET Status = ? WHERE OrderID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean deleteOrder(int orderId) throws SQLException {
        String query = "DELETE FROM Orders WHERE OrderID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}