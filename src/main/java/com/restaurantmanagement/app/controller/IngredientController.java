package com.restaurantmanagement.app.controller;

import com.restaurantmanagement.app.entity.Ingredient;
import com.restaurantmanagement.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
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
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        unitColumn.setCellValueFactory(cellData -> cellData.getValue().unitProperty());
        stockColumn.setCellValueFactory(cellData -> cellData.getValue().stockProperty().asObject());
        minStockColumn.setCellValueFactory(cellData -> cellData.getValue().minStockProperty().asObject());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().pricePerUnitProperty().asObject());

        searchField.setOnKeyReleased(event -> handleSearch());
        addButtonToTable();
        loadCategories();

        categoryFilter.setOnAction(event -> handleFilterByCategory());
        loadAllIngredients();
    }

    private void addButtonToTable() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonsBox = new HBox(10, editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    Ingredient ingredient = getTableView().getItems().get(getIndex());
                    handleEditIngredient(ingredient);
                });

                deleteButton.setOnAction(event -> {
                    Ingredient ingredient = getTableView().getItems().get(getIndex());
                    handleDeleteIngredient(ingredient);
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

    @FXML
    private void handleAddIngredient() {
        IngredientForm dialog = new IngredientForm(null);
        Optional<Ingredient> result = dialog.showAndWait();
        result.ifPresent(ingredient -> ingredientList.add(ingredient));
    }

    @FXML
    private void handleEditIngredient(Ingredient ingredient) {
        if (ingredient == null) {
            showAlert("Select an ingredient to edit.");
            return;
        }

        IngredientForm dialog = new IngredientForm(ingredient);
        Optional<Ingredient> result = dialog.showAndWait();

        result.ifPresent(updated -> {
            int index = ingredientList.indexOf(ingredient);
            ingredientList.set(index, updated);

            loadAllIngredients();

            if (ingredientTable != null) {
                ingredientTable.refresh();
            }

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

    @FXML
    private void handleDeleteIngredient(Ingredient selected) {
        if (selected == null) {
            showAlert("Select an ingredient to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            ingredientList.remove(selected);

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

    @FXML
    private void handleAddCategory() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Category");
        dialog.setHeaderText("Enter new category name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(categoryName -> {
            if (categoryName.trim().isEmpty()) {
                showAlert("Category name cannot be empty!");
                return;
            }

            String sql = "INSERT INTO IngredientCategories (Name) VALUES (?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, categoryName);
                int affectedRows = stmt.executeUpdate();

                if (affectedRows > 0) {
                    showAlert("Added category: " + categoryName);
                    loadCategories();
                } else {
                    showAlert("Failed to add category!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error adding category: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleFilterByCategory() {
        String selectedCategory = categoryFilter.getValue();

        ObservableList<Ingredient> filteredList = FXCollections.observableArrayList();
        if (selectedCategory == null || selectedCategory.equals("All")) {
            filteredList = ingredientList;
        } else {
            for (Ingredient ingredient : ingredientList) {
                if (selectedCategory.equals(ingredient.getCategory())) {
                    filteredList.add(ingredient);
                }
            }
        }

        ingredientTable.setItems(filteredList);
        addButtonToTable();
    }

    private void loadCategories() {
        ObservableList<String> categories = FXCollections.observableArrayList("All");
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
        String sql = "INSERT INTO DailyStock (IngredientID, Name, Unit, Stock, MinStock, PricePerUnit, Date) " +
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
                showAlert("Daily stock saved successfully!");
            } else {
                showAlert("No data was saved.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error saving daily stock: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }
}