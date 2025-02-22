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

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private ComboBox<String> roleComboBox;

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

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || roleString == null) {
            showAlert("Notification", "Please fill in all information.", AlertType.WARNING);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Notification", "Passwords do not match.", AlertType.WARNING);
            return;
        }

        if (userService.getUserByUsername(username) != null) {
            showAlert("Notification", "Account already exists.", AlertType.WARNING);
            return;
        }

        String passwordHash = HashingUtils.hashPasswordWithSalt(password);
        Role role = Role.valueOf(roleString.toUpperCase());
        User newUser = new User(0, username, passwordHash, role, null);
        userRepository.saveUser(newUser);

        showAlert("Notification", "Registration successful!", AlertType.INFORMATION);

        navigateToLogin();
    }

    @FXML
    private void handleBack() {
        navigateToLogin();
    }

    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurantmanagement/fxml/Login.fxml"));
            Parent loginView = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loginView));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot open the login screen.", AlertType.ERROR);
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