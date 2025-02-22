package com.restaurantmanagement.app.controller;

import com.restaurantmanagement.app.entity.User;
import com.restaurantmanagement.app.entity.Role;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class DashboardController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private ImageView roleImageView;
    @FXML
    private Button logoutButton;
    @FXML
    private Button menuButton;
    @FXML
    private Button orderButton;
    @FXML
    private Button employeeButton;
    @FXML
    private Button inventoryButton;
    @FXML
    private Button reportButton;
    @FXML
    private StackPane contentArea;

    private User currentUser;

    public void initializeDashboard(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + currentUser.getUsername());
        updateRoleImage();
        configureButtonsVisibility();
    }

    private void updateRoleImage() {
        String imagePath = null;
        if (currentUser.getRole() == Role.MANAGER) {
            imagePath = "/com/restaurantmanagement/picture/manager.png";
        } else if (currentUser.getRole() == Role.CHEF) {
            imagePath = "/com/restaurantmanagement/picture/chef.png";
        } else if (currentUser.getRole() == Role.CASHIER) {
            imagePath = "/com/restaurantmanagement/picture/cashier.png";
        }
        if (imagePath != null) {
            imagePath = imagePath.replace("@", "");
            URL resource = getClass().getResource(imagePath);
            if (resource != null) {
                roleImageView.setImage(new Image(resource.toExternalForm()));
            } else {
                System.err.println("Image resource not found: " + imagePath);
            }
        }
    }

    private void configureButtonsVisibility() {
        menuButton.setVisible(false);
        orderButton.setVisible(false);
        employeeButton.setVisible(false);
        inventoryButton.setVisible(false);
        reportButton.setVisible(false);

        switch (currentUser.getRole()) {
            case MANAGER:
                menuButton.setVisible(true);
                orderButton.setVisible(true);
                employeeButton.setVisible(true);
                inventoryButton.setVisible(true);
                reportButton.setVisible(true);
                break;
            case CHEF:
                menuButton.setVisible(true);
                inventoryButton.setVisible(true);
                break;
            case CASHIER:
                menuButton.setVisible(true);
                orderButton.setVisible(true);
                break;
        }
    }

    private void loadView(String fxmlFile) {
        try {
            URL resource = getClass().getResource("/com/restaurantmanagement/fxml/" + fxmlFile);
            if (resource == null) {
                throw new IllegalStateException("Cannot find FXML file: " + fxmlFile);
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load view: " + fxmlFile, Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleOrders() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addOrderItem = new MenuItem("Add Order");
        MenuItem viewOrdersItem = new MenuItem("View Orders");
        addOrderItem.setOnAction(e -> openOrderWindow("addOrder.fxml", "Add Order"));
        viewOrdersItem.setOnAction(e -> openOrderWindow("viewOrders.fxml", "View Orders"));
        contextMenu.getItems().addAll(addOrderItem, viewOrdersItem);
        contextMenu.show(orderButton, orderButton.getScene().getWindow().getX() + orderButton.getLayoutX(),
                orderButton.getScene().getWindow().getY() + orderButton.getLayoutY() + orderButton.getHeight());
    }

    private void openOrderWindow(String fxmlFile, String title) {
        try {
            URL resource = getClass().getResource("/com/restaurantmanagement/fxml/" + fxmlFile);
            if (resource == null) {
                throw new IllegalStateException("Cannot find FXML file: " + fxmlFile);
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("Error", "Cannot load view: " + fxmlFile, Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleMenu() {
        if (currentUser.getRole() == Role.MANAGER) {
            loadView("menu.fxml");
        } else if (currentUser.getRole() == Role.CHEF) {
            loadView("viewMenu.fxml");
        } else {
            showAlert("Access Denied", "You don't have permission to access the menu.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleEmployees() {
        loadView("employee.fxml");
    }

    @FXML
    private void handleInventory() {
        loadView("main.fxml");
    }

    @FXML
    private void handleReport() {
        loadView("report.fxml");
    }

    public void setRoleImage(String role) {
        String imagePath = switch (role) {
            case "Manager" -> "/images/manager.png";
            case "Chef" -> "/images/chef.png";
            case "Cashier" -> "/images/cashier.png";
            default -> "/images/default.png";
        };

        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            roleImageView.setImage(image);
        } catch (Exception e) {
            System.out.println("Error loading role image: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/restaurantmanagement/fxml/Login.fxml"));
            Parent loginView = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(loginView));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("Error", "Cannot load login view.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
