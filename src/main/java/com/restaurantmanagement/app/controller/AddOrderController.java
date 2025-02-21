package com.restaurantmanagement.app.controller;

import com.restaurantmanagement.app.entity.OrderDetail;
import com.restaurantmanagement.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.*;

public class AddOrderController extends Stage {

    @FXML
    private TextField itemIdField, quantityField, priceField;
    @FXML
    private TableView<OrderDetail> orderTable;
    @FXML
    private TableColumn<OrderDetail, Integer> itemIdColumn, quantityColumn;
    @FXML
    private TableColumn<OrderDetail, BigDecimal> priceColumn;
    @FXML
    private Label totalAmountLabel;

    private final ObservableList<OrderDetail> orderDetails = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Gán cellValueFactory cho các cột trong TableView
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<>("itemID"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        orderTable.setItems(orderDetails);
        totalAmountLabel.setText("0.00");
    }

    @FXML
    public void handleAddItem() {
        try {
            int itemId = Integer.parseInt(itemIdField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
            BigDecimal price = new BigDecimal(priceField.getText());

            OrderDetail newItem = new OrderDetail(0, 0, itemId, quantity, price);
            orderDetails.add(newItem);
            updateTotalAmount();

            itemIdField.clear();
            quantityField.clear();
            priceField.clear();
        } catch (NumberFormatException e) {
            showAlert("Invalid input! Please enter valid numbers.");
        }
    }

    @FXML
    public void handleDeleteItem() {
        OrderDetail selectedItem = orderTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            orderDetails.remove(selectedItem);
            updateTotalAmount();
        } else {
            showAlert("Please select an item to delete.");
        }
    }

    private void updateTotalAmount() {
        BigDecimal total = orderDetails.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalAmountLabel.setText(total.toString());
    }

    @FXML
    public void handleSaveOrder() {
        if (orderDetails.isEmpty()) {
            showAlert("Order cannot be empty.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            // Kiểm tra sự tồn tại của ItemID trong MenuItems trước khi thêm vào OrderDetails
            for (OrderDetail detail : orderDetails) {
                int itemId = detail.getItemID();
                String checkItemSql = "SELECT COUNT(*) FROM MenuItems WHERE ItemID = ?";
                try (PreparedStatement checkItemStatement = connection.prepareStatement(checkItemSql)) {
                    checkItemStatement.setInt(1, itemId);
                    ResultSet resultSet = checkItemStatement.executeQuery();
                    resultSet.next();
                    int count = resultSet.getInt(1);
                    if (count == 0) {
                        showAlert("Item ID " + itemId + " does not exist in MenuItems.");
                        return; // Dừng việc lưu đơn hàng
                    }
                }
            }

            // Thêm đơn hàng vào bảng Orders
            String sqlOrder = "INSERT INTO Orders (TotalAmount, Status) VALUES (?, 'PENDING')";
            try (PreparedStatement orderStatement = connection.prepareStatement(sqlOrder, PreparedStatement.RETURN_GENERATED_KEYS)) {
                BigDecimal totalAmount = new BigDecimal(totalAmountLabel.getText());
                orderStatement.setBigDecimal(1, totalAmount);
                orderStatement.executeUpdate();

                var generatedKeys = orderStatement.getGeneratedKeys();
                int orderId = generatedKeys.next() ? generatedKeys.getInt(1) : 0;

                // Thêm chi tiết đơn hàng vào bảng OrderDetails
                String sqlDetail = "INSERT INTO OrderDetails (OrderID, ItemID, Quantity, Price) VALUES (?, ?, ?, ?)";
                try (PreparedStatement detailStatement = connection.prepareStatement(sqlDetail)) {
                    for (OrderDetail detail : orderDetails) {
                        detailStatement.setInt(1, orderId);
                        detailStatement.setInt(2, detail.getItemID());
                        detailStatement.setInt(3, detail.getQuantity());
                        detailStatement.setBigDecimal(4, detail.getPrice());
                        detailStatement.executeUpdate();
                    }
                }
            }

            connection.commit();
            showAlert("Order added successfully!");
            close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error adding order: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }
}
