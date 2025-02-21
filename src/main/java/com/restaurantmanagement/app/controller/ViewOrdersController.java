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
        // Lấy kết nối từ DatabaseConnection và khởi tạo OrderViewService
        Connection connection = DatabaseConnection.getConnection();
        orderViewService = new OrderViewService(connection);

        // Thiết lập các cột trong TableView
        orderIdColumn.setCellValueFactory(cellData -> cellData.getValue().orderIDProperty().asObject());
        totalAmountColumn.setCellValueFactory(cellData -> cellData.getValue().totalAmountProperty());
        orderDateColumn.setCellValueFactory(cellData -> cellData.getValue().orderDateProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        loadOrders();
    }

    @FXML
    public void handleRefresh() {
        loadOrders();
    }

    @FXML
    public void handleUpdateStatus() {
        // Lấy giá trị Order ID và Status từ giao diện người dùng
        String selectedOrderId = orderIdField.getText();
        String selectedStatus = statusComboBox.getValue();

        // Kiểm tra xem có đủ thông tin cần thiết không
        if (selectedOrderId.isEmpty() || selectedStatus == null) {
            showAlert("Please provide both Order ID and Status.");
            return;
        }

        try {
            // Chuyển Order ID từ String sang int
            int orderId = Integer.parseInt(selectedOrderId);
            // Gọi service để cập nhật trạng thái
            boolean updated = orderViewService.updateOrderStatus(orderId, selectedStatus);
            if (updated) {
                showAlert("Order status updated successfully!");
                loadOrders(); // Tải lại danh sách đơn hàng sau khi cập nhật
            } else {
                showAlert("Failed to update order status.");
            }
        } catch (NumberFormatException e) {
            // Xử lý khi Order ID không hợp lệ
            showAlert("Invalid Order ID. Please enter a valid number.");
        }
    }

    // Phương thức để tải danh sách đơn hàng và hiển thị lên giao diện
    private void loadOrders() {
        try {
            List<Order> orders = orderViewService.getOrders(); // Lấy danh sách đơn hàng từ service
            ObservableList<Order> items = FXCollections.observableArrayList(orders);
            ordersTableView.setItems(items); // Cập nhật TableView với danh sách đơn hàng
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error loading orders: " + e.getMessage()); // Thông báo lỗi nếu có lỗi khi tải danh sách
        }
    }

    // Phương thức hiển thị thông báo lỗi
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }
}
