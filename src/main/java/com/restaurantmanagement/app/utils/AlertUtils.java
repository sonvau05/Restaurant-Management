package com.restaurantmanagement.app.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertUtils {

    public static void showInfo(String message) {
        showAlert(AlertType.INFORMATION, "Information", message);
    }

    public static void showError(String message) {
        showAlert(AlertType.ERROR, "Error", message);
    }

    public static void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
