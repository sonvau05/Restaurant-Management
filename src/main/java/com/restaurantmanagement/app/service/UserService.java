package com.restaurantmanagement.app.service;

import com.restaurantmanagement.app.entity.User;
import com.restaurantmanagement.app.entity.Role;
import com.restaurantmanagement.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {

    public User getUserByUsername(String username) {
        User user = null;
        String sql = "SELECT * FROM Users WHERE Username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("UserID");
                String passwordHash = rs.getString("PasswordHash");
                Role role = Role.valueOf(rs.getString("Role"));
                String createdAt = rs.getString("CreatedAt");
                user = new User(userId, username, passwordHash, role, createdAt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
}
