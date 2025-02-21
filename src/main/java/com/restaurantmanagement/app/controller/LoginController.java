package com.restaurantmanagement.app.controller;

import com.restaurantmanagement.app.entity.User;
import com.restaurantmanagement.app.service.UserService;
import com.restaurantmanagement.app.utils.HashingUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private UserService userService;

    public LoginController() {
        this.userService = new UserService();
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Thông báo", "Vui lòng nhập đầy đủ thông tin đăng nhập.", AlertType.WARNING);
            return;
        }

        // Validate user credentials
        User user = userService.getUserByUsername(username);

        if (user != null && HashingUtils.checkPassword(password, user.getPasswordHash())) {
            System.out.println("Đăng nhập thành công!");

            // Load Dashboard
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurantmanagement/fxml/Dashboard.fxml"));
                Parent dashboard = loader.load();
                DashboardController dashboardController = loader.getController();
                dashboardController.initializeDashboard(user);
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(dashboard));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Lỗi", "Không thể mở màn hình Dashboard.", AlertType.ERROR);
            }
        } else {
            System.out.println("Thông tin đăng nhập không chính xác!");
            showAlert("Thông báo", "Tên người dùng hoặc mật khẩu không đúng.", AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurantmanagement/fxml/Register.fxml"));
            Parent registerView = loader.load();
            // Lấy stage hiện tại từ nút đăng nhập (hoặc một thành phần nào đó thuộc scene hiện tại)
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(registerView));
            stage.setTitle("Đăng ký tài khoản");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể mở màn hình đăng ký.", Alert.AlertType.ERROR);
        }
    }
}