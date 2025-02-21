package com.restaurantmanagement.app.repository;

import com.restaurantmanagement.app.entity.MenuItems;
import com.restaurantmanagement.app.entity.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.restaurantmanagement.database.DatabaseConnection.getConnection;

public class MenuRepository {

    // Lấy tất cả các món ăn từ cơ sở dữ liệu
    public List<MenuItems> getAllMenuItems() {
        List<MenuItems> menuItems = new ArrayList<>();
        String query = "SELECT m.ItemID, m.Name, m.Price, c.Name AS Category, m.Description " +
                "FROM MenuItems m " +
                "JOIN Categories c ON m.CategoryID = c.CategoryID";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("ItemID");
                String name = resultSet.getString("Name");
                double price = resultSet.getDouble("Price");
                String category = resultSet.getString("Category");
                String description = resultSet.getString("Description"); // Lấy mô tả

                // Tạo đối tượng MenuItems và thêm vào danh sách
                MenuItems menuItem = new MenuItems(id, name, price, category, description);
                menuItems.add(menuItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return menuItems;
    }

    // Tìm kiếm món ăn theo tên
    public List<MenuItems> searchMenuItems(String keyword) {
        List<MenuItems> menuItems = new ArrayList<>();
        String query = "SELECT m.ItemID, m.Name, m.Price, c.Name AS Category, m.Description " +
                "FROM MenuItems m " +
                "JOIN Categories c ON m.CategoryID = c.CategoryID " +
                "WHERE m.Name LIKE ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, "%" + keyword + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("ItemID");
                    String name = resultSet.getString("Name");
                    double price = resultSet.getDouble("Price");
                    String category = resultSet.getString("Category");
                    String description = resultSet.getString("Description");

                    // Tạo đối tượng MenuItems và thêm vào danh sách
                    MenuItems menuItem = new MenuItems(id, name, price, category, description);
                    menuItems.add(menuItem);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return menuItems;
    }

    // Thêm món ăn mới
    public boolean addMenuItem(String name, double price, String description, int categoryId) {
        String query = "INSERT INTO MenuItems (Name, Price, Description, CategoryID) VALUES (?, ?, ?, ?)";
        boolean success = false;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, name);
            statement.setDouble(2, price);
            statement.setString(3, description);
            statement.setInt(4, categoryId); // Thêm categoryId vào câu truy vấn
            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    // Cập nhật món ăn
    public boolean updateMenuItem(int itemId, String newName, double newPrice, String newDescription, int categoryId) {
        String query = "UPDATE MenuItems SET Name = ?, Price = ?, Description = ?, CategoryID = ? WHERE ItemID = ?";
        boolean success = false;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, newName);
            statement.setDouble(2, newPrice);
            statement.setString(3, newDescription);
            statement.setInt(4, categoryId); // Cập nhật categoryId
            statement.setInt(5, itemId);
            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    // Xóa món ăn
    public boolean deleteMenuItem(int itemId) {
        String query = "DELETE FROM MenuItems WHERE ItemID = ?";
        boolean success = false;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, itemId);
            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    // Lấy tất cả các danh mục món ăn
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT CategoryID, Name FROM Categories";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("CategoryID");
                String name = resultSet.getString("Name");

                Category category = new Category(id, name);
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
}
