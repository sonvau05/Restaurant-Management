package com.restaurantmanagement.app.controller;

import com.restaurantmanagement.app.entity.MenuItem;
import com.restaurantmanagement.app.entity.Category;
import com.restaurantmanagement.app.service.MenuService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MenuController {
    @FXML private TextField searchField;
    @FXML private Button categoriesButton;
    @FXML private TableView<MenuItem> menuItemsTable;
    @FXML private TableColumn<MenuItem, Integer> colId;
    @FXML private TableColumn<MenuItem, String> colName;
    @FXML private TableColumn<MenuItem, String> colCategory;
    @FXML private TableColumn<MenuItem, String> colDescription;
    @FXML private TableColumn<MenuItem, Double> colPrice;

    private final MenuService menuService = new MenuService();
    private ObservableList<MenuItem> menuItemsList = FXCollections.observableArrayList();
    private ObservableList<Category> categoriesList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getItemID()).asObject());
        colName.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        colCategory.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(getCategoryName(cellData.getValue().getCategoryID())));
        colDescription.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));
        colPrice.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPrice().doubleValue()).asObject());
        loadMenuItems();
        loadCategories();
    }

    private void loadMenuItems() {
        menuItemsList.setAll(menuService.getAllMenuItems());
        menuItemsTable.setItems(menuItemsList);
    }

    private void loadCategories() {
        categoriesList.setAll(menuService.getAllCategories());
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String searchQuery = searchField.getText();
        if (!searchQuery.isEmpty()) {
            menuItemsList.setAll(menuService.searchMenuItems(searchQuery));
        } else {
            loadMenuItems();
        }
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Add New Menu Item");
        nameDialog.setHeaderText("Enter the name of the new menu item");
        nameDialog.setContentText("Menu item name:");
        Optional<String> nameResult = nameDialog.showAndWait();
        if (nameResult.isPresent()) {
            String itemName = nameResult.get();
            TextInputDialog priceDialog = new TextInputDialog();
            priceDialog.setTitle("Add Menu Item Price");
            priceDialog.setHeaderText("Enter the price of the menu item");
            priceDialog.setContentText("Price:");
            Optional<String> priceResult = priceDialog.showAndWait();
            if (priceResult.isPresent()) {
                String priceString = priceResult.get();
                double price;
                try {
                    price = Double.parseDouble(priceString);
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Price Error", "Invalid price. Please enter a number.");
                    return;
                }
                TextInputDialog descriptionDialog = new TextInputDialog();
                descriptionDialog.setTitle("Add Menu Item Description");
                descriptionDialog.setHeaderText("Enter the description of the menu item");
                descriptionDialog.setContentText("Description:");
                Optional<String> descriptionResult = descriptionDialog.showAndWait();
                if (descriptionResult.isPresent()) {
                    String description = descriptionResult.get();
                    ChoiceDialog<Category> categoryDialog = new ChoiceDialog<>(null, categoriesList);
                    categoryDialog.setTitle("Select Menu Item Category");
                    categoryDialog.setHeaderText("Select Menu Item Category");
                    categoryDialog.setContentText("Category:");
                    Optional<Category> categoryResult = categoryDialog.showAndWait();
                    if (categoryResult.isPresent()) {
                        Category selectedCategory = categoryResult.get();
                        int categoryId = selectedCategory.getCategoryID();
                        menuService.addMenuItem(itemName, price, description, categoryId);
                        loadMenuItems();
                    }
                }
            }
        }
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        MenuItem selectedItem = menuItemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            TextInputDialog nameDialog = new TextInputDialog(selectedItem.getName());
            nameDialog.setTitle("Edit Menu Item");
            nameDialog.setHeaderText("Enter the new name");
            nameDialog.setContentText("Menu item name:");
            Optional<String> nameResult = nameDialog.showAndWait();
            if (nameResult.isPresent()) {
                String newName = nameResult.get();
                TextInputDialog priceDialog = new TextInputDialog(String.valueOf(selectedItem.getPrice()));
                priceDialog.setTitle("Edit Menu Item Price");
                priceDialog.setHeaderText("Enter the new price");
                priceDialog.setContentText("Price:");
                Optional<String> priceResult = priceDialog.showAndWait();
                if (priceResult.isPresent()) {
                    String priceString = priceResult.get();
                    double newPrice;
                    try {
                        newPrice = Double.parseDouble(priceString);
                    } catch (NumberFormatException e) {
                        showAlert(Alert.AlertType.ERROR, "Price Error", "Invalid price. Please enter a number.");
                        return;
                    }
                    TextInputDialog descriptionDialog = new TextInputDialog(selectedItem.getDescription());
                    descriptionDialog.setTitle("Edit Menu Item Description");
                    descriptionDialog.setHeaderText("Enter the new description");
                    descriptionDialog.setContentText("Description:");
                    Optional<String> descriptionResult = descriptionDialog.showAndWait();
                    if (descriptionResult.isPresent()) {
                        String newDescription = descriptionResult.get();
                        ChoiceDialog<Category> categoryDialog = new ChoiceDialog<>(null, categoriesList);
                        categoryDialog.setTitle("Select Menu Item Category");
                        categoryDialog.setHeaderText("Select Menu Item Category");
                        categoryDialog.setContentText("Category:");
                        Optional<Category> categoryResult = categoryDialog.showAndWait();
                        if (categoryResult.isPresent()) {
                            Category selectedCategory = categoryResult.get();
                            int categoryId = selectedCategory.getCategoryID();
                            menuService.updateMenuItem(selectedItem.getItemID(), newName, newPrice, newDescription, categoryId);
                            loadMenuItems();
                        }
                    }
                }
            }
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Notification", "Please select a menu item to edit.");
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        MenuItem selectedItem = menuItemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Deletion");
            confirmationAlert.setHeaderText("Are you sure you want to delete this menu item?");
            confirmationAlert.setContentText("Menu Item: " + selectedItem.getName());
            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                menuService.deleteMenuItem(selectedItem.getItemID());
                loadMenuItems();
            }
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Notification", "Please select a menu item to delete.");
        }
    }

    @FXML
    private void handleCategories(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurantmanagement/fxml/categories.fxml"));
            AnchorPane categoriesPane = loader.load();
            StackPane contentArea = (StackPane) categoriesButton.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().setAll(categoriesPane);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Không tìm thấy contentArea trong Dashboard!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Không thể tải categories: " + e.getMessage());
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}