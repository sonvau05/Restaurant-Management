package com.restaurantmanagement.app.controller;

import com.restaurantmanagement.app.entity.InventoryLog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class ManagerInventoryController {
    @FXML
    private DatePicker dateFilterField;
    @FXML
    private TextField ingredientFilterField, transactionTypeFilterField;
    @FXML
    private Button filterButton;
    @FXML
    private TextField cancelUnitField;
    @FXML
    private TextField unitField, supplierField, quantityField, priceField, cancelQuantityField, cancelNoteField;
    @FXML
    private ComboBox<String> ingredientComboBox, cancelIngredientComboBox;
    @FXML
    private TableView<InventoryLog> inventoryLogTable;
    @FXML
    private TableColumn<InventoryLog, String> logIngredientColumn, logTransactionTypeColumn, logNoteColumn, logUnitColumn, logDateColumn;
    @FXML
    private TableColumn<InventoryLog, Float> logQuantityColumn;
    @FXML
    private TableColumn<InventoryLog, Double> logPriceColumn;
    @FXML
    private Button addPurchaseButton, cancelIngredientButton;

    private Connection conn;

    public void initialize() {
        connectDB();
        loadIngredients();
        loadInventoryLog();

        // Gán giá trị cho cột bảng
        logIngredientColumn.setCellValueFactory(cellData -> cellData.getValue().ingredientProperty());
        logTransactionTypeColumn.setCellValueFactory(cellData -> cellData.getValue().transactionTypeProperty());
        logQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        logPriceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        logNoteColumn.setCellValueFactory(cellData -> cellData.getValue().noteProperty());
        logUnitColumn.setCellValueFactory(cellData -> cellData.getValue().unitProperty());
        logDateColumn.setCellValueFactory(cellData -> cellData.getValue().logDateProperty());

    }

    private void connectDB() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Restaurant", "root", "123456");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi kết nối", "Không thể kết nối đến cơ sở dữ liệu!", Alert.AlertType.ERROR);
        }
    }

    private void loadIngredients() {
        String query = "SELECT Name FROM Ingredients";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            ObservableList<String> ingredients = FXCollections.observableArrayList();
            while (rs.next()) {
                ingredients.add(rs.getString("Name"));
            }
            ingredientComboBox.setItems(ingredients);
            cancelIngredientComboBox.setItems(ingredients);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadInventoryLog() {
        ObservableList<InventoryLog> logList = FXCollections.observableArrayList();
        String query = """
        SELECT i.Name AS Ingredient, t.TypeName, l.Quantity, l.Price, l.Note, l.TransactionDate, l.Unit
        FROM InventoryTransactions l
        JOIN Ingredients i ON l.IngredientID = i.IngredientID
        JOIN TransactionTypes t ON l.TransactionTypeID = t.TransactionTypeID
        ORDER BY l.TransactionDate DESC
    """;

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                logList.add(new InventoryLog(
                        rs.getString("Ingredient"),
                        rs.getString("TypeName"),
                        rs.getFloat("Quantity"),
                        rs.getString("Unit"),
                        rs.getDouble("Price"),
                        rs.getString("Note"),
                        rs.getString("TransactionDate")
                ));
            }
            inventoryLogTable.setItems(logList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddPurchase() {
        String supplier = supplierField.getText();
        String ingredientName = ingredientComboBox.getValue();
        String quantityText = quantityField.getText();
        String priceText = priceField.getText();
        String unit = unitField.getText().trim();

        if (ingredientName == null || quantityText.isEmpty() || priceText.isEmpty() || unit.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập đầy đủ thông tin, bao gồm đơn vị!", Alert.AlertType.ERROR);
            return;
        }

        try {
            float quantity = Float.parseFloat(quantityText);
            double price = Double.parseDouble(priceText);

            if (quantity <= 0 || price < 0) {
                showAlert("Lỗi", "Số lượng và giá phải lớn hơn 0!", Alert.AlertType.ERROR);
                return;
            }

            String getIngredientIdQuery = "SELECT IngredientID FROM Ingredients WHERE Name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(getIngredientIdQuery)) {
                stmt.setString(1, ingredientName);
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    showAlert("Lỗi", "Không tìm thấy nguyên liệu!", Alert.AlertType.ERROR);
                    return;
                }
                int ingredientId = rs.getInt("IngredientID");

                String getTransactionTypeIdQuery = "SELECT TransactionTypeID FROM TransactionTypes WHERE LOWER(TypeName) = LOWER(?)";
                try (PreparedStatement stmt2 = conn.prepareStatement(getTransactionTypeIdQuery)) {
                    stmt2.setString(1, "Nhập kho");
                    ResultSet rs2 = stmt2.executeQuery();

                    if (!rs2.next()) {
                        showAlert("Lỗi", "Loại giao dịch 'Nhập kho' không tồn tại!", Alert.AlertType.ERROR);
                        return;
                    }
                    int transactionTypeId = rs2.getInt("TransactionTypeID");

                    String insertTransactionQuery = """
                INSERT INTO InventoryTransactions (TransactionTypeID, SupplierName, IngredientID, Quantity, Price, Note, Unit)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
                    try (PreparedStatement stmt3 = conn.prepareStatement(insertTransactionQuery)) {
                        stmt3.setInt(1, transactionTypeId);
                        stmt3.setString(2, supplier);
                        stmt3.setInt(3, ingredientId);
                        stmt3.setFloat(4, quantity);
                        stmt3.setDouble(5, price);
                        stmt3.setString(6, "Nhập hàng từ nhà cung cấp: " + supplier);
                        stmt3.setString(7, unit);
                        stmt3.executeUpdate();
                    }
                }
            }
            showAlert("Thành công", "Nhập kho thành công!", Alert.AlertType.INFORMATION);
            loadInventoryLog(); // Cập nhật bảng nhật ký kho

            // Xóa dữ liệu trong các trường nhập
            supplierField.clear();
            ingredientComboBox.getSelectionModel().clearSelection();
            quantityField.clear();
            priceField.clear();
            unitField.clear();
        } catch (SQLException | NumberFormatException e) {
            showAlert("Lỗi", "Dữ liệu không hợp lệ hoặc lỗi kết nối!", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelIngredient() {
        String ingredientName = cancelIngredientComboBox.getValue();
        String quantityText = cancelQuantityField.getText();
        String note = cancelNoteField.getText();
        String unit = cancelUnitField.getText().trim();

        if (ingredientName == null || quantityText.isEmpty() || note.isEmpty() || unit.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập đầy đủ thông tin, bao gồm đơn vị!", Alert.AlertType.ERROR);
            return;
        }

        try {
            float quantity = Float.parseFloat(quantityText);
            if (quantity <= 0) {
                showAlert("Lỗi", "Số lượng phải lớn hơn 0!", Alert.AlertType.ERROR);
                return;
            }

            // Lấy IngredientID từ Ingredients
            String getIngredientIdQuery = "SELECT IngredientID FROM Ingredients WHERE Name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(getIngredientIdQuery)) {
                stmt.setString(1, ingredientName);
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    showAlert("Lỗi", "Không tìm thấy nguyên liệu!", Alert.AlertType.ERROR);
                    return;
                }
                int ingredientId = rs.getInt("IngredientID");

                // Lấy TransactionTypeID cho "Hủy nguyên liệu"
                String getTransactionTypeIdQuery = "SELECT TransactionTypeID FROM TransactionTypes WHERE LOWER(TypeName) = LOWER(?)";
                try (PreparedStatement stmt2 = conn.prepareStatement(getTransactionTypeIdQuery)) {
                    stmt2.setString(1, "Hủy nguyên liệu");
                    ResultSet rs2 = stmt2.executeQuery();

                    if (!rs2.next()) {
                        showAlert("Lỗi", "Loại giao dịch 'Hủy nguyên liệu' không tồn tại!", Alert.AlertType.ERROR);
                        return;
                    }
                    int transactionTypeId = rs2.getInt("TransactionTypeID");

                    // Chỉ lưu giao dịch vào InventoryTransactions, không cập nhật Stock
                    String insertTransactionQuery = """
                INSERT INTO InventoryTransactions (TransactionTypeID, IngredientID, Quantity, Price, Note, Unit)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
                    try (PreparedStatement stmt3 = conn.prepareStatement(insertTransactionQuery)) {
                        stmt3.setInt(1, transactionTypeId);
                        stmt3.setInt(2, ingredientId);
                        stmt3.setFloat(3, quantity);
                        stmt3.setDouble(4, 0.0); // Giá = 0 vì đây là hủy nguyên liệu
                        stmt3.setString(5, note);
                        stmt3.setString(6, unit);
                        stmt3.executeUpdate();
                    }
                }
            }

            showAlert("Thành công", "Hủy nguyên liệu thành công!", Alert.AlertType.INFORMATION);
            loadInventoryLog();

            // Xóa dữ liệu trong các trường nhập
            cancelIngredientComboBox.getSelectionModel().clearSelection();
            cancelQuantityField.clear();
            cancelNoteField.clear();
            cancelUnitField.clear();
        } catch (SQLException | NumberFormatException e) {
            showAlert("Lỗi", "Dữ liệu không hợp lệ hoặc lỗi kết nối!", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }


    @FXML
    private void handleFilter() {
        String ingredientFilter = ingredientFilterField.getText().trim();
        String dateFilter = "";
        if (dateFilterField.getValue() != null) {
            dateFilter = dateFilterField.getValue().toString();
        }
        String transactionTypeFilter = transactionTypeFilterField.getText().trim();

        loadInventoryLog(ingredientFilter, dateFilter, transactionTypeFilter);
    }

    private void loadInventoryLog(String ingredientFilter, String dateFilter, String transactionTypeFilter) {
        ObservableList<InventoryLog> logList = FXCollections.observableArrayList();

        StringBuilder query = new StringBuilder("""
        SELECT i.Name AS Ingredient, t.TypeName, l.Quantity, l.Price, l.Note, l.TransactionDate, l.Unit
        FROM InventoryTransactions l
        JOIN Ingredients i ON l.IngredientID = i.IngredientID
        JOIN TransactionTypes t ON l.TransactionTypeID = t.TransactionTypeID
        WHERE 1=1
    """);

        if (!ingredientFilter.isEmpty()) {
            query.append(" AND i.Name LIKE ?");
        }
        if (!dateFilter.isEmpty()) {
            query.append(" AND l.TransactionDate LIKE ?");
        }
        if (!transactionTypeFilter.isEmpty()) {
            query.append(" AND t.TypeName LIKE ?");
        }

        query.append(" ORDER BY l.TransactionDate DESC");

        try (PreparedStatement stmt = conn.prepareStatement(query.toString())) {
            int paramIndex = 1;

            if (!ingredientFilter.isEmpty()) {
                stmt.setString(paramIndex++, "%" + ingredientFilter + "%");
            }
            if (!dateFilter.isEmpty()) {
                stmt.setString(paramIndex++, "%" + dateFilter + "%");
            }
            if (!transactionTypeFilter.isEmpty()) {
                stmt.setString(paramIndex++, "%" + transactionTypeFilter + "%");
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logList.add(new InventoryLog(
                            rs.getString("Ingredient"),
                            rs.getString("TypeName"),
                            rs.getFloat("Quantity"),
                            rs.getString("Unit"),
                            rs.getDouble("Price"),
                            rs.getString("Note"),
                            rs.getString("TransactionDate")
                    ));
                }
                inventoryLogTable.setItems(logList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
