package com.restaurantmanagement.app.service;

import com.restaurantmanagement.app.entity.Order;
import com.restaurantmanagement.app.entity.OrderDetail;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderViewService {
    private final Connection connection;

    public OrderViewService(Connection connection) {
        this.connection = connection;
    }

    public List<Order> getOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT o.OrderID, o.TotalAmount, o.OrderDate, o.Status, od.OrderDetailID, od.ItemID, od.Quantity, od.Price, mi.Name, c.Name AS CategoryName FROM Orders o LEFT JOIN OrderDetails od ON o.OrderID = od.OrderID LEFT JOIN MenuItems mi ON od.ItemID = mi.ItemID LEFT JOIN Categories c ON mi.CategoryID = c.CategoryID";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            Map<Integer, Order> orderMap = new HashMap<>();
            while (rs.next()) {
                int orderId = rs.getInt("OrderID");
                Order order = orderMap.get(orderId);
                if (order == null) {
                    order = new Order(orderId, rs.getBigDecimal("TotalAmount"), rs.getTimestamp("OrderDate"), rs.getString("Status"), new ArrayList<>());
                    orderMap.put(orderId, order);
                }
                String itemName = rs.getString("Name");
                String categoryName = rs.getString("CategoryName");
                if (itemName != null) {
                    order.getOrderDetails().add(new OrderDetail(rs.getInt("OrderDetailID"), orderId, rs.getInt("ItemID"), itemName, categoryName, rs.getInt("Quantity"), rs.getBigDecimal("Price")));
                }
            }
            orders.addAll(orderMap.values());
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