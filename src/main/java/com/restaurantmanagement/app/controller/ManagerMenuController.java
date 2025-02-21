package com.restaurantmanagement.app.controller;

import com.restaurantmanagement.app.entity.MenuItems;
import com.restaurantmanagement.app.entity.Category;
import com.restaurantmanagement.app.service.MenuService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Optional;

import static com.restaurantmanagement.app.utils.AlertUtils.showAlert;

public class ManagerMenuController {

    @FXML
    private TextField searchField;
    @FXML
    private Button categoriesButton;

    @FXML
    private TableView<MenuItems> menuItemsTable;

    @FXML
    private TableColumn<MenuItems, Integer> colId;
    @FXML
    private TableColumn<MenuItems, String> colName;
    @FXML
    private TableColumn<MenuItems, String> colCategory;
    @FXML
    private TableColumn<MenuItems, String> colDescription;
    @FXML
    private TableColumn<MenuItems, Double> colPrice;

    private MenuService managerMenuService = new MenuService();
    private ObservableList<MenuItems> menuItemsList = FXCollections.observableArrayList();
    private ObservableList<Category> categoriesList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Set up table columns
        // Giả sử MenuItems có các property: idProperty(), nameProperty(), categoryProperty(), descriptionProperty(), priceProperty()
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colCategory.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        colDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        colPrice.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());

        // Load menu items vào bảng
        loadMenuItems();

        // Load danh mục (giả sử phương thức getAllCategories() tồn tại trong MenuService)
        categoriesList.setAll(managerMenuService.getAllCategories());
    }

    private void loadMenuItems() {
        menuItemsList.setAll(managerMenuService.getAllMenuItems());
        menuItemsTable.setItems(menuItemsList);
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String searchQuery = searchField.getText();
        if (!searchQuery.isEmpty()) {
            menuItemsList.setAll(managerMenuService.searchMenuItems(searchQuery));
        } else {
            loadMenuItems();
        }
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Thêm món ăn mới");
        dialog.setHeaderText("Nhập tên và giá món ăn mới");
        dialog.setContentText("Tên món ăn:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String itemName = result.get();

            // Lấy giá
            TextInputDialog priceDialog = new TextInputDialog();
            priceDialog.setTitle("Thêm giá món ăn");
            priceDialog.setHeaderText("Nhập giá món ăn");
            priceDialog.setContentText("Giá món ăn:");

            Optional<String> priceResult = priceDialog.showAndWait();
            if (priceResult.isPresent()) {
                String priceString = priceResult.get();
                double price;

                // Kiểm tra nếu giá nhập vào là số hợp lệ
                try {
                    price = Double.parseDouble(priceString);
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi giá", "Giá món ăn không hợp lệ. Vui lòng nhập một số.");
                    return;
                }

                // Lấy mô tả
                TextInputDialog descriptionDialog = new TextInputDialog();
                descriptionDialog.setTitle("Thêm mô tả món ăn");
                descriptionDialog.setHeaderText("Nhập mô tả món ăn");
                descriptionDialog.setContentText("Mô tả:");

                Optional<String> descriptionResult = descriptionDialog.showAndWait();
                if (descriptionResult.isPresent()) {
                    String description = descriptionResult.get();

                    // Chọn danh mục bằng ChoiceDialog
                    ChoiceDialog<Category> categoryDialog = new ChoiceDialog<>(null, categoriesList);
                    categoryDialog.setTitle("Chọn danh mục món ăn");
                    categoryDialog.setHeaderText("Chọn danh mục món ăn");
                    categoryDialog.setContentText("Danh mục:");

                    categoryDialog.setResultConverter(dialogButton -> {
                        if (dialogButton == ButtonType.OK) {
                            return categoryDialog.getSelectedItem();
                        }
                        return null;
                    });

                    Optional<Category> categoryResult = categoryDialog.showAndWait();
                    if (categoryResult.isPresent()) {
                        Category selectedCategory = categoryResult.get();
                        int categoryId = selectedCategory.getCategoryID();

                        // Thêm món ăn sử dụng service
                        managerMenuService.addMenuItem(itemName, price, description, categoryId);
                        loadMenuItems();
                    }
                }
            }
        }
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        MenuItems selectedItem = menuItemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            TextInputDialog nameDialog = new TextInputDialog(selectedItem.getName());
            nameDialog.setTitle("Sửa món ăn");
            nameDialog.setHeaderText("Nhập tên mới");
            nameDialog.setContentText("Tên món ăn:");

            Optional<String> nameResult = nameDialog.showAndWait();
            if (nameResult.isPresent()) {
                String newName = nameResult.get();

                TextInputDialog priceDialog = new TextInputDialog(String.valueOf(selectedItem.getPrice()));
                priceDialog.setTitle("Sửa giá món ăn");
                priceDialog.setHeaderText("Nhập giá mới");
                priceDialog.setContentText("Giá món ăn:");

                Optional<String> priceResult = priceDialog.showAndWait();
                if (priceResult.isPresent()) {
                    String priceString = priceResult.get();
                    double newPrice;

                    // Kiểm tra nếu giá nhập vào là số hợp lệ
                    try {
                        newPrice = Double.parseDouble(priceString);
                    } catch (NumberFormatException e) {
                        showAlert(Alert.AlertType.ERROR, "Lỗi giá", "Giá món ăn không hợp lệ. Vui lòng nhập một số.");
                        return;
                    }

                    TextInputDialog descriptionDialog = new TextInputDialog(selectedItem.getDescription());
                    descriptionDialog.setTitle("Sửa mô tả món ăn");
                    descriptionDialog.setHeaderText("Nhập mô tả mới");
                    descriptionDialog.setContentText("Mô tả:");

                    Optional<String> descriptionResult = descriptionDialog.showAndWait();
                    if (descriptionResult.isPresent()) {
                        String newDescription = descriptionResult.get();

                        // Chọn danh mục bằng ChoiceDialog
                        ChoiceDialog<Category> categoryDialog = new ChoiceDialog<>(null, categoriesList);
                        categoryDialog.setTitle("Chọn danh mục món ăn");
                        categoryDialog.setHeaderText("Chọn danh mục món ăn");
                        categoryDialog.setContentText("Danh mục:");

                        categoryDialog.setResultConverter(dialogButton -> {
                            if (dialogButton == ButtonType.OK) {
                                return categoryDialog.getSelectedItem();
                            }
                            return null;
                        });

                        Optional<Category> categoryResult = categoryDialog.showAndWait();
                        if (categoryResult.isPresent()) {
                            Category selectedCategory = categoryResult.get();
                            int categoryId = selectedCategory.getCategoryID();

                            managerMenuService.updateMenuItem(selectedItem.getId(), newName, newPrice, newDescription, categoryId);
                            loadMenuItems();
                        }
                    }
                }
            }
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Vui lòng chọn món ăn để chỉnh sửa.");
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        MenuItems selectedItem = menuItemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Xác nhận xóa");
            confirmationAlert.setHeaderText("Bạn có chắc chắn muốn xóa món ăn này?");
            confirmationAlert.setContentText("Món ăn: " + selectedItem.getName());

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                managerMenuService.deleteMenuItems(selectedItem.getId());
                loadMenuItems();
            }
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Vui lòng chọn món ăn để xóa.");
        }
    }

    @FXML
    private void handleCategories(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurantmanagement/fxml/categories.fxml"));
            AnchorPane categoriesPane = loader.load();
            Scene currentScene = categoriesButton.getScene();
            currentScene.setRoot(categoriesPane);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể tải danh mục");
            alert.setContentText("Đã xảy ra lỗi khi chuyển đến danh mục.");
            alert.showAndWait();
        }
    }
}
