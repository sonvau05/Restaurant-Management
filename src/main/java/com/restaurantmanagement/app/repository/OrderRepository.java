package com.restaurantmanagement.app.repository;

import com.restaurantmanagement.app.entity.Order;       // Bạn cần định nghĩa entity Order
import com.restaurantmanagement.app.entity.OrderDetail; // Bạn cần định nghĩa entity OrderDetail
import com.restaurantmanagement.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private final Connection connection;

    public OrderRepository(Connection connection) {
        this.connection = connection;
    }

    // Lưu đơn hàng và trả về OrderID được tạo ra
    public int addOrder(double totalAmount) throws SQLException {
        String sql = "INSERT INTO Orders (TotalAmount, Status) VALUES (?, 'PENDING')";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, totalAmount);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }
        }
    }

    // Lưu chi tiết đơn hàng
    public boolean addOrderDetail(int orderId, int itemId, int quantity, double price) throws SQLException {
        String sql = "INSERT INTO OrderDetails (OrderID, ItemID, Quantity, Price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, itemId);
            ps.setInt(3, quantity);
            ps.setDouble(4, price);
            return ps.executeUpdate() > 0;
        }
    }

    // Lấy danh sách đơn hàng
    public List<Order> getOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM Orders";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("OrderID"),
                        rs.getBigDecimal("TotalAmount"),
                        rs.getTimestamp("OrderDate"),
                        rs.getString("Status")
                );
                orders.add(order);
            }
        }
        return orders;
    }
}
