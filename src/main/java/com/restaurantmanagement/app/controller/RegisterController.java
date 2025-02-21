package com.restaurantmanagement.app.controller;

import com.restaurantmanagement.app.entity.User;
import com.restaurantmanagement.app.entity.Role;
import com.restaurantmanagement.app.service.UserService;
import com.restaurantmanagement.app.repository.UserRepository;
import com.restaurantmanagement.app.utils.HashingUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleComboBox;

    private UserService userService;
    private UserRepository userRepository;

    public RegisterController() {
        this.userService = new UserService();
        this.userRepository = new UserRepository();
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String roleString = roleComboBox.getValue();

        // Kiểm tra nếu các trường không được nhập đầy đủ
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || roleString == null) {
            showAlert("Thông báo", "Vui lòng nhập đầy đủ thông tin.", AlertType.WARNING);
            return;
        }

        // Kiểm tra nếu mật khẩu và xác nhận mật khẩu không khớp
        if (!password.equals(confirmPassword)) {
            showAlert("Thông báo", "Mật khẩu không khớp.", AlertType.WARNING);
            return;
        }

        // Kiểm tra xem tài khoản đã tồn tại chưa
        if (userService.getUserByUsername(username) != null) {
            showAlert("Thông báo", "Tài khoản đã tồn tại.", AlertType.WARNING);
            return;
        }

        // Mã hóa mật khẩu
        String passwordHash = HashingUtils.hashPasswordWithSalt(password);

        // Lưu người dùng vào cơ sở dữ liệu
        Role role = Role.valueOf(roleString.toUpperCase());
        User newUser = new User(0, username, passwordHash, role, null);
        userRepository.saveUser(newUser);

        // Hiển thị thông báo thành công
        showAlert("Thông báo", "Đăng ký thành công!", AlertType.INFORMATION);

        // Quay lại màn hình đăng nhập
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurantmanagement/fxml/Login.fxml"));
            Parent loginView = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loginView));
            stage.setTitle("Đăng nhập");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể mở màn hình đăng nhập.", AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
