package com.restaurantmanagement.app.controller;

import com.restaurantmanagement.app.entity.Ingredient;
import com.restaurantmanagement.database.DatabaseConnection;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class IngredientForm {
    private final TextField nameField = new TextField();
    private final TextField unitField = new TextField();
    private final TextField stockField = new TextField();
    private final TextField minStockField = new TextField();
    private final TextField priceField = new TextField();
    private final ComboBox<String> categoryBox = new ComboBox<>();

    private final Map<String, Integer> categoryMap = new HashMap<>();

    private Ingredient ingredient;

    public IngredientForm(Ingredient ingredient) {
        this.ingredient = ingredient;


        loadCategories();

        if (ingredient != null) {
            nameField.setText(ingredient.getName());
            unitField.setText(ingredient.getUnit());
            stockField.setText(String.valueOf(ingredient.getStock()));
            minStockField.setText(String.valueOf(ingredient.getMinStock()));
            priceField.setText(String.valueOf(ingredient.getPricePerUnit()));


            categoryBox.setValue(ingredient.getCategory());

        }
    }

    private void loadCategories() {
        String query = "SELECT * FROM ingredientcategories";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int categoryId = resultSet.getInt("CategoryID");
                String name = resultSet.getString("name");

                categoryMap.put(name, categoryId);
                categoryBox.getItems().add(name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể tải danh mục từ CSDL!");
        }
    }

    private String getCategoryNameById(int categoryId) {
        return categoryMap.entrySet().stream()
                .filter(entry -> entry.getValue() == categoryId)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public Optional<Ingredient> showAndWait() {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(ingredient == null ? "Thêm nguyên liệu" : "Sửa nguyên liệu");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(8);
        grid.setHgap(10);

        grid.add(new Label("Tên:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Đơn vị:"), 0, 1);
        grid.add(unitField, 1, 1);
        grid.add(new Label("Tồn kho:"), 0, 2);
        grid.add(stockField, 1, 2);
        grid.add(new Label("Tồn tối thiểu:"), 0, 3);
        grid.add(minStockField, 1, 3);
        grid.add(new Label("Giá/Đơn vị:"), 0, 4);
        grid.add(priceField, 1, 4);
        grid.add(new Label("Danh mục:"), 0, 5);
        grid.add(categoryBox, 1, 5);

        Button saveButton = new Button("Lưu");
        saveButton.setOnAction(event -> {
            if (validateInput()) {
                saveIngredient();
                dialogStage.close();
            }
        });

        grid.add(saveButton, 1, 6);
        Scene scene = new Scene(grid, 350, 350);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();

        return Optional.ofNullable(ingredient);
    }

    private boolean validateInput() {
        if (nameField.getText().isEmpty() || unitField.getText().isEmpty() ||
                stockField.getText().isEmpty() || minStockField.getText().isEmpty() ||
                priceField.getText().isEmpty() || categoryBox.getValue() == null) {
            showAlert("Lỗi nhập liệu", "Vui lòng điền đầy đủ thông tin!");
            return false;
        }
        return true;
    }

    private void saveIngredient() {
        int categoryId = categoryMap.get(categoryBox.getValue());

        if (ingredient == null) {
            String query = "INSERT INTO Ingredients (name, unit, stock, MinStock, PricePerUnit, CategoryID) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, nameField.getText());
                statement.setString(2, unitField.getText());
                statement.setDouble(3, Double.parseDouble(stockField.getText()));
                statement.setDouble(4, Double.parseDouble(minStockField.getText()));
                statement.setDouble(5, Double.parseDouble(priceField.getText()));
                statement.setInt(6, categoryId);

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert("Thành công", "Nguyên liệu đã được thêm!");
                } else {
                    showAlert("Lỗi", "Không thể thêm nguyên liệu!");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Lỗi", "Không thể lưu nguyên liệu!");
            }

        } else {
            String query = "UPDATE Ingredients SET name=?, unit=?, stock=?, MinStock=?, PricePerUnit=?, CategoryID=? WHERE IngredientID=?";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, nameField.getText());
                statement.setString(2, unitField.getText());
                statement.setDouble(3, Double.parseDouble(stockField.getText()));
                statement.setDouble(4, Double.parseDouble(minStockField.getText()));
                statement.setDouble(5, Double.parseDouble(priceField.getText()));
                statement.setInt(6, categoryId);
                statement.setInt(7, ingredient.getId());

                System.out.println("Updating Ingredient ID: " + ingredient.getId());

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Cập nhật nguyên liệu thành công!");

                    String checkQuery = "SELECT * FROM Ingredients WHERE IngredientID=?";
                    try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                        checkStatement.setInt(1, ingredient.getId());
                        ResultSet resultSet = checkStatement.executeQuery();

                        if (resultSet.next()) {
                            System.out.println("Dữ liệu sau khi cập nhật: ID=" + resultSet.getInt("IngredientID") +
                                    ", Name=" + resultSet.getString("name") +
                                    ", Unit=" + resultSet.getString("unit") +
                                    ", Stock=" + resultSet.getDouble("stock") +
                                    ", MinStock=" + resultSet.getDouble("MinStock") +
                                    ", PricePerUnit=" + resultSet.getDouble("PricePerUnit") +
                                    ", CategoryID=" + resultSet.getInt("CategoryID"));
                        }
                    }
                }


            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Lỗi", "Không thể cập nhật nguyên liệu!");
            }
        }
    }



    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
