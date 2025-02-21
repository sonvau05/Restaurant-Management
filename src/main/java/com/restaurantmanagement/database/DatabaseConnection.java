package com.restaurantmanagement.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/Restaurant";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Kiểm tra dòng này
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Kết nối thành công!");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver MySQL không được tìm thấy!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối đến MySQL!");
            e.printStackTrace();
        }
        return conn;
    }
}
