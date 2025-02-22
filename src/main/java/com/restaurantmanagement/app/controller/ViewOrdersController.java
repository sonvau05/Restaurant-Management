package com.restaurantmanagement.app.controller;

import com.restaurantmanagement.app.entity.Order;
import com.restaurantmanagement.app.service.OrderViewService;
import com.restaurantmanagement.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class ViewOrdersController {

    @FXML
    private TableView<Order> ordersTableView;

    @FXML
    private TableColumn<Order, Integer> orderIdColumn;

    @FXML
    private TableColumn<Order, BigDecimal> totalAmountColumn;

    @FXML
    private TableColumn<Order, Timestamp> orderDateColumn;

    @FXML
    private TableColumn<Order, String> statusColumn;

    @FXML
    private TextField orderIdField;

    @FXML
    private ComboBox<String> statusComboBox;

    private OrderViewService orderViewService;

    @FXML
    public void initialize() {
        Connection connection = DatabaseConnection.getConnection();
        orderViewService = new OrderViewService(connection);

        orderIdColumn.setCellValueFactory(cellData
                -> cellData.getValue().orderIDProperty().asObject());
        totalAmountColumn.setCellValueFactory(cellData
                -> cellData.getValue().totalAmountProperty());
        orderDateColumn.setCellValueFactory(cellData
                -> cellData.getValue().orderDateProperty());
        statusColumn.setCellValueFactory(cellData
                -> cellData.getValue().statusProperty());

        loadOrders();
    }

    @FXML
    public void handleRefresh() {
        loadOrders();
    }

    @FXML
    public void handleUpdateStatus() {
        String selectedOrderId = orderIdField.getText();
        String selectedStatus = statusComboBox.getValue();

        if (selectedOrderId.isEmpty() || selectedStatus == null) {
            showAlert("Please provide both Order ID and Status.");
            return;
        }

        try {
            int orderId = Integer.parseInt(selectedOrderId);
            boolean updated = orderViewService.updateOrderStatus(orderId, selectedStatus);
            if (updated) {
                showAlert("Order status updated successfully!");
                loadOrders();
            } else {
                showAlert("Failed to update order status.");
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Order ID. Please enter a valid number.");
        }
    }

    private void loadOrders() {
        try {
            List<Order> orders = orderViewService.getOrders();
            ObservableList<Order> items = FXCollections.observableArrayList(orders);
            ordersTableView.setItems(items);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error loading orders: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }
}
