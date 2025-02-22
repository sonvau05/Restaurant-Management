package com.restaurantmanagement.app.repository;

import com.restaurantmanagement.app.entity.Order;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderViewRepository {

    private final Connection connection;

    public OrderViewRepository(Connection connection) {
        this.connection = connection;
    }

    public List<Order> getOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT OrderID, TotalAmount, OrderDate, Status FROM Orders";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int orderID = resultSet.getInt("OrderID");
                BigDecimal totalAmount = resultSet.getBigDecimal("TotalAmount");
                Timestamp orderDate = resultSet.getTimestamp("OrderDate");
                String status = resultSet.getString("Status");
                orders.add(new Order(orderID, totalAmount, orderDate, status));
            }
        }
        return orders;
    }

    public boolean updateOrderStatus(int orderId, String status) throws SQLException {
        String query = "UPDATE Orders SET Status = ? WHERE OrderID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, status);
            statement.setInt(2, orderId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
