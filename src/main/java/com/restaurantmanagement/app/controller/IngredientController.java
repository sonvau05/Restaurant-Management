package com.restaurantmanagement.app.controller;

import com.restaurantmanagement.app.entity.Ingredient;
import com.restaurantmanagement.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class IngredientController {
    @FXML private ComboBox<String> categoryFilter;

    @FXML private TextField searchField;
    @FXML private TableView<Ingredient> ingredientTable;
    @FXML private TableColumn<Ingredient, Integer> idColumn;
    @FXML private TableColumn<Ingredient, String> nameColumn;
    @FXML private TableColumn<Ingredient, String> unitColumn;
    @FXML private TableColumn<Ingredient, Double> stockColumn;
    @FXML private TableColumn<Ingredient, Double> minStockColumn;
    @FXML private TableColumn<Ingredient, Double> priceColumn;
    @FXML private TableColumn<Ingredient, Void> actionColumn;

    private final ObservableList<Ingredient> ingredientList = FXCollections.observableArrayList();

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            ingredientTable.setItems(ingredientList);
            return;
        }

        ObservableList<Ingredient> filteredList = FXCollections.observableArrayList();
        for (Ingredient ingredient : ingredientList) {
            if (ingredient.getName().toLowerCase().contains(keyword)) {
                filteredList.add(ingredient);
            }
        }

        ingredientTable.setItems(filteredList);
    }

    @FXML
    public void initialize() {
        // Thiết lập giá trị cho các cột
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        unitColumn.setCellValueFactory(cellData -> cellData.getValue().unitProperty());
        stockColumn.setCellValueFactory(cellData -> cellData.getValue().stockProperty().asObject());
        minStockColumn.setCellValueFactory(cellData -> cellData.getValue().minStockProperty().asObject());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().pricePerUnitProperty().asObject());

        searchField.setOnKeyReleased(event -> handleSearch());
        addButtonToTable();

        loadCategories();  // <--- Thêm dòng này để tải danh mục

        // Đăng ký sự kiện khi chọn danh mục trong ComboBox
        categoryFilter.setOnAction(event -> handleFilterByCategory());

        // Tải dữ liệu từ database
        loadAllIngredients();
    }

    private void addButtonToTable() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Sửa");
            private final Button deleteButton = new Button("Xóa");
            private final HBox buttonsBox = new HBox(10, editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    Ingredient ingredient = getTableView().getItems().get(getIndex());
                    handleEditIngredient(ingredient); // Đã sửa lỗi
                });

                deleteButton.setOnAction(event -> {
                    Ingredient ingredient = getTableView().getItems().get(getIndex());
                    handleDeleteIngredient(ingredient);  // Gọi phương thức đã sửa
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonsBox);
                }
            }
        });
    }


    private void loadAllIngredients() {
        ingredientList.clear();
        String sql = "SELECT i.IngredientID, i.Name, c.Name AS Category, i.Unit, i.Stock, i.MinStock, i.PricePerUnit " +
                "FROM Ingredients i LEFT JOIN IngredientCategories c ON i.CategoryID = c.CategoryID";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ingredientList.add(new Ingredient(
                        rs.getInt("IngredientID"),
                        rs.getString("Name"),
                        rs.getString("Category"),
                        rs.getString("Unit"),
                        rs.getDouble("Stock"),
                        rs.getDouble("MinStock"),
                        rs.getDouble("PricePerUnit")
                ));
            }

            ingredientTable.setItems(ingredientList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Thêm nguyên liệu
    @FXML
    private void handleAddIngredient() {
        IngredientForm dialog = new IngredientForm(null);
        Optional<Ingredient> result = dialog.showAndWait();
        result.ifPresent(ingredient -> ingredientList.add(ingredient));
    }


    @FXML
    private void handleEditIngredient(Ingredient ingredient) {
        if (ingredient == null) {
            showAlert("Chọn một nguyên liệu để sửa.");
            return;
        }

        IngredientForm dialog = new IngredientForm(ingredient);
        Optional<Ingredient> result = dialog.showAndWait();

        result.ifPresent(updated -> {
            int index = ingredientList.indexOf(ingredient);
            ingredientList.set(index, updated); // Cập nhật nguyên liệu trong danh sách

            loadAllIngredients(); // Load lại toàn bộ danh sách để làm mới bảng

            if (ingredientTable != null) {
                ingredientTable.refresh(); // Refresh thủ công bảng nếu ingredientTable tồn tại
            }

            // Cập nhật database
            String sql = "UPDATE Ingredients SET Name = ?, Unit = ?, Stock = ?, MinStock = ?, PricePerUnit = ?, CategoryID = " +
                    "(SELECT CategoryID FROM IngredientCategories WHERE Name = ?) WHERE IngredientID = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, updated.getName());
                stmt.setString(2, updated.getUnit());
                stmt.setDouble(3, updated.getStock());
                stmt.setDouble(4, updated.getMinStock());
                stmt.setDouble(5, updated.getPricePerUnit());
                stmt.setString(6, updated.getCategory());
                stmt.setInt(7, updated.getId());
                stmt.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }



    // Xóa nguyên liệu
    @FXML
    private void handleDeleteIngredient(Ingredient selected) {
        if (selected == null) {
            showAlert("Chọn một nguyên liệu để xóa.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn xóa?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            ingredientList.remove(selected);

            // Xóa khỏi database
            String sql = "DELETE FROM Ingredients WHERE IngredientID = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, selected.getId());
                stmt.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Thêm danh mục nguyên liệu
    @FXML
    private void handleAddCategory() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Thêm danh mục");
        dialog.setHeaderText("Nhập tên danh mục mới:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(categoryName -> {
            if (categoryName.trim().isEmpty()) {
                showAlert("Tên danh mục không được để trống!");
                return;
            }

            String sql = "INSERT INTO IngredientCategories (Name) VALUES (?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, categoryName);
                int affectedRows = stmt.executeUpdate();

                if (affectedRows > 0) {
                    showAlert("Đã thêm danh mục: " + categoryName);
                    loadCategories();  // Cập nhật danh sách sau khi thêm
                } else {
                    showAlert("Thêm danh mục thất bại!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Lỗi khi thêm danh mục: " + e.getMessage());
            }
        });
    }


    @FXML
    private void handleFilterByCategory() {
        String selectedCategory = categoryFilter.getValue();

        ObservableList<Ingredient> filteredList = FXCollections.observableArrayList();
        if (selectedCategory == null || selectedCategory.equals("Tất cả")) {
            filteredList = ingredientList;  // Hiển thị lại toàn bộ danh sách
        } else {
            for (Ingredient ingredient : ingredientList) {
                if (selectedCategory.equals(ingredient.getCategory())) {
                    filteredList.add(ingredient);
                }
            }
        }

        ingredientTable.setItems(filteredList);

        // Gọi lại phương thức thêm nút vào bảng sau khi thay đổi danh sách
        addButtonToTable();
    }



    private void loadCategories() {
        ObservableList<String> categories = FXCollections.observableArrayList("Tất cả");
        String sql = "SELECT Name FROM IngredientCategories";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("Name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        categoryFilter.setItems(categories);
        categoryFilter.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleSaveDailyStock() {
        String sql = "INSERT INTO DailyStock (IngredientID, Name,  Unit, Stock, MinStock, PricePerUnit, Date) " +
                "VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Ingredient ingredient : ingredientList) {
                stmt.setInt(1, ingredient.getId());
                stmt.setString(2, ingredient.getName());
                stmt.setString(3, ingredient.getUnit());
                stmt.setDouble(4, ingredient.getStock());
                stmt.setDouble(5, ingredient.getMinStock());
                stmt.setDouble(6, ingredient.getPricePerUnit());

                stmt.addBatch();

            }

            int[] affectedRows = stmt.executeBatch();

            if (affectedRows.length > 0) {
                showAlert("Đã lưu tồn kho thành công!");
            } else {
                showAlert("Không có dữ liệu nào được lưu.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi khi lưu tồn kho: " + e.getMessage());
        }
    }

    // Hiển thị thông báo
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }

}