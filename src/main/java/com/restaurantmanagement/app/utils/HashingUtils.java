package com.restaurantmanagement.app.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashingUtils {

    private static final String SALT = "my_secret_salt";  // Salt cố định

    // Hàm băm mật khẩu với salt
    public static String hashPasswordWithSalt(String password) {
        try {
            // Tạo SHA-256 digest
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            // Thêm salt vào mật khẩu trước khi băm
            String saltedPassword = password + SALT;
            byte[] hashedBytes = messageDigest.digest(saltedPassword.getBytes());

            // Trả về chuỗi đã mã hóa (băm) dưới dạng Base64
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Không thể mã hóa mật khẩu", e);
        }
    }

    // Kiểm tra mật khẩu đã nhập với mật khẩu đã băm
    public static boolean checkPassword(String password, String storedHashedPassword) {
        // Mã hóa mật khẩu được nhập với salt và so sánh với mật khẩu đã lưu
        String hashedPassword = hashPasswordWithSalt(password);
        return hashedPassword.equals(storedHashedPassword);
    }

    // Hàm mã hóa mật khẩu trong cơ sở dữ liệu, sử dụng salt cố định
    public static String hashPasswordForDatabase(String password) {
        // Sử dụng SHA-256 để băm mật khẩu và mã hóa Base64
        return hashPasswordWithSalt(password);
    }
}
