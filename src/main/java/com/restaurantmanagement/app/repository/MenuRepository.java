package com.restaurantmanagement.app.repository;

import com.restaurantmanagement.app.entity.MenuItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.restaurantmanagement.database.DatabaseConnection.getConnection;

public class MenuRepository {
    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> menuItems = new ArrayList<>();
        String query = "SELECT m.ItemID, m.Name, m.Price, m.Description, m.CategoryID, c.Name AS CategoryName FROM MenuItems m JOIN Categories c ON m.CategoryID = c.CategoryID";
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("ItemID");
                String name = resultSet.getString("Name");
                BigDecimal price = resultSet.getBigDecimal("Price");
                String description = resultSet.getString("Description");
                int categoryId = resultSet.getInt("CategoryID");
                String categoryName = resultSet.getString("CategoryName");
                MenuItem menuItem = new MenuItem(id, name, categoryId, price, description);
                menuItems.add(menuItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return menuItems;
    }

    public List<MenuItem> searchMenuItems(String keyword) {
        List<MenuItem> menuItems = new ArrayList<>();
        String query = "SELECT m.ItemID, m.Name, m.Price, m.Description, m.CategoryID, c.Name AS CategoryName FROM MenuItems m JOIN Categories c ON m.CategoryID = c.CategoryID WHERE m.Name LIKE ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "%" + keyword + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("ItemID");
                    String name = resultSet.getString("Name");
                    BigDecimal price = resultSet.getBigDecimal("Price");
                    String description = resultSet.getString("Description");
                    int categoryId = resultSet.getInt("CategoryID");
                    String categoryName = resultSet.getString("CategoryName");
                    MenuItem menuItem = new MenuItem(id, name, categoryId, price, description);
                    menuItems.add(menuItem);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return menuItems;
    }

    public boolean addMenuItem(String name, double price, String description, int categoryId) {
        String query = "INSERT INTO MenuItems (Name, Price, Description, CategoryID) VALUES (?, ?, ?, ?)";
        boolean success = false;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setDouble(2, price);
            statement.setString(3, description);
            statement.setInt(4, categoryId);
            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    public boolean updateMenuItem(int itemId, String newName, double newPrice, String newDescription, int categoryId) {
        String query = "UPDATE MenuItems SET Name = ?, Price = ?, Description = ?, CategoryID = ? WHERE ItemID = ?";
        boolean success = false;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, newName);
            statement.setDouble(2, newPrice);
            statement.setString(3, newDescription);
            statement.setInt(4, categoryId);
            statement.setInt(5, itemId);
            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

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
}