package com.restaurantmanagement.app.service;

import com.restaurantmanagement.app.entity.Order;
import com.restaurantmanagement.app.repository.OrderViewRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderViewService {
    private final OrderViewRepository orderViewRepository;

    public OrderViewService(Connection connection) {
        this.orderViewRepository = new OrderViewRepository(connection);
    }

    public List<Order> getOrders() throws SQLException {
        return orderViewRepository.getOrders();
    }

    public boolean updateOrderStatus(int orderId, String selectedStatus) {
        try {
            // Gọi phương thức từ repository để cập nhật trạng thái
            return orderViewRepository.updateOrderStatus(orderId, selectedStatus);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
