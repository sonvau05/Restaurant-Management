package com.restaurantmanagement.app.controller;

import com.restaurantmanagement.app.entity.Category;
import com.restaurantmanagement.app.entity.OrderDetail;
import com.restaurantmanagement.app.entity.MenuItem;
import com.restaurantmanagement.app.service.MenuService;
import com.restaurantmanagement.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

public class AddOrderController extends Stage {
    @FXML private ComboBox<String> itemNameComboBox;
    @FXML private TextField quantityField;
    @FXML private TableView<OrderDetail> orderTable;
    @FXML private TableColumn<OrderDetail, String> itemNameColumn;
    @FXML private TableColumn<OrderDetail, String> categoryColumn;
    @FXML private TableColumn<OrderDetail, Integer> quantityColumn;
    @FXML private TableColumn<OrderDetail, BigDecimal> priceColumn;
    @FXML private Label totalAmountLabel;

    private final ObservableList<OrderDetail> orderDetails = FXCollections.observableArrayList();
    private final ObservableList<String> menuItemNames = FXCollections.observableArrayList();
    private final MenuService menuService;

    public AddOrderController() {
        Connection connection = null;
        connection = DatabaseConnection.getConnection();
        this.menuService = new MenuService();
    }

    @FXML
    public void initialize() {
        loadMenuItems();
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        itemNameComboBox.setItems(menuItemNames);
        orderTable.setItems(orderDetails);
        totalAmountLabel.setText("0.00");
    }

    private void loadMenuItems() {
        List<MenuItem> items = menuService.getAllMenuItems();
        for (MenuItem item : items) {
            menuItemNames.add(item.getName());
        }
    }

    @FXML
    public void handleAddItem() {
        String selectedItemName = itemNameComboBox.getValue();
        String quantityText = quantityField.getText();
        if (selectedItemName == null || quantityText.isEmpty()) {
            showAlert("Please select a meal and enter quantity!");
            return;
        }
        try {
            int quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                showAlert("Quantity must be greater than 0!");
                return;
            }
            MenuItem menuItem = menuService.getMenuItemByName(selectedItemName);
            if (menuItem != null) {
                int itemId = menuItem.getItemID();
                BigDecimal price = menuItem.getPrice();
                String categoryName = getCategoryName(menuItem.getCategoryID());
                OrderDetail newItem = new OrderDetail(0, 0, itemId, selectedItemName, categoryName, quantity, price);
                orderDetails.add(newItem);
                updateTotalAmount();
            }
            quantityField.clear();
            itemNameComboBox.getSelectionModel().clearSelection();
        } catch (NumberFormatException e) {
            showAlert("Invalid quantity! Please enter a valid number.");
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
        BigDecimal total = orderDetails.stream().map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
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
                        return;
                    }
                }
            }
            String sqlOrder = "INSERT INTO Orders (TotalAmount, Status) VALUES (?, 'PENDING')";
            try (PreparedStatement orderStatement = connection.prepareStatement(sqlOrder, PreparedStatement.RETURN_GENERATED_KEYS)) {
                BigDecimal totalAmount = new BigDecimal(totalAmountLabel.getText());
                orderStatement.setBigDecimal(1, totalAmount);
                orderStatement.executeUpdate();
                ResultSet generatedKeys = orderStatement.getGeneratedKeys();
                int orderId = generatedKeys.next() ? generatedKeys.getInt(1) : 0;
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

    private String getCategoryName(int categoryId) {
        List<Category> categories = menuService.getAllCategories();
        for (Category category : categories) {
            if (category.getCategoryID() == categoryId) {
                return category.getName();
            }
        }
        return "Unknown";
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }
}