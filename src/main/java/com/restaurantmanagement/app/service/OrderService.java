package com.restaurantmanagement.app.service;

import com.restaurantmanagement.app.entity.Order;
import com.restaurantmanagement.app.repository.OrderRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(Connection connection) {
        this.orderRepository = new OrderRepository(connection);
    }

    public int addOrder(double totalAmount) throws SQLException {
        return orderRepository.addOrder(totalAmount);
    }

    public boolean addOrderDetail(int orderId, int itemId, int quantity, double price) throws SQLException {
        return orderRepository.addOrderDetail(orderId, itemId, quantity, price);
    }

    public List<Order> getOrders() throws SQLException {
        return orderRepository.getOrders();
    }
}
