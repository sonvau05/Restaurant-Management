package com.restaurantmanagement.app.controller;

import com.restaurantmanagement.app.entity.User;
import com.restaurantmanagement.app.entity.Role;
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

    @FXML private Label welcomeLabel;
    @FXML private ImageView roleImageView;
    @FXML private Button logoutButton; // Nút log out trong vùng top
    @FXML private Button menuButton;
    @FXML private Button orderButton;
    @FXML private Button employeeButton;
    @FXML private Button inventoryButton;
    @FXML private Button reportButton;
    @FXML private StackPane contentArea; // Vùng chứa nội dung được load từ FXML con

    private User currentUser;

    /**
     * Phương thức này được gọi sau khi FXML được load và nhận đối tượng User.
     * Nó sẽ cập nhật lời chào, hình ảnh vai trò và hiển thị các nút menu dựa trên vai trò.
     */
    public void initializeDashboard(User user) {
        this.currentUser = user;
        // Cập nhật lời chào
        welcomeLabel.setText("Welcome, " + currentUser.getUsername());
        // Cập nhật hình ảnh vai trò
        updateRoleImage();
        // Điều chỉnh hiển thị các nút menu
        configureButtonsVisibility();
        // Bạn có thể load giao diện mặc định vào contentArea nếu cần.
    }

    /**
     * Cập nhật hình ảnh vai trò dựa vào currentUser.getRole().
     * Đường dẫn hình ảnh theo định dạng: @../picture/manager.png, @../picture/chef.png, hoặc @../picture/cashier.png.
     */
    private void updateRoleImage() {
        String imagePath = null;
        if (currentUser.getRole() == Role.MANAGER) {
            imagePath = "@../picture/manager.png";
        } else if (currentUser.getRole() == Role.CHEF) {
            imagePath = "@../picture/chef.png";
        } else if (currentUser.getRole() == Role.CASHIER) {
            imagePath = "@../picture/cashier.png";
        }
        if (imagePath != null) {
            // Loại bỏ ký tự '@' và tìm resource từ classpath
            imagePath = imagePath.replace("@", "");
            URL resource = getClass().getResource(imagePath);
            if (resource != null) {
                roleImageView.setImage(new Image(resource.toExternalForm()));
            } else {
                System.err.println("Image resource not found: " + imagePath);
            }
        }
    }

    /**
     * Ẩn tất cả các nút menu ban đầu, sau đó bật hiển thị dựa trên vai trò của người dùng.
     */
    private void configureButtonsVisibility() {
        menuButton.setVisible(false);
        orderButton.setVisible(false);
        employeeButton.setVisible(false);
        inventoryButton.setVisible(false);
        reportButton.setVisible(false);
        // Nút Log Out được hiển thị ở vùng top luôn nên không cần thay đổi tại đây.

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

    /**
     * Load giao diện từ file FXML vào vùng contentArea.
     * Các file FXML được lấy từ đường dẫn: "/com/restaurantmanagement/fxml/" + fxmlFile.
     */
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
    private void handleMenu() {
        if (currentUser.getRole() == Role.MANAGER) {
            loadView("manager_menu.fxml");
        } else {
            loadView("menu.fxml");
        }
    }

    @FXML
    private void handleOrders() {
        // Sử dụng ContextMenu để hiển thị các tùy chọn Order
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addOrderItem = new MenuItem("Add Order");
        MenuItem viewOrdersItem = new MenuItem("View Orders");
        addOrderItem.setOnAction(e -> openOrderWindow("addOrder.fxml", "Add Order"));
        viewOrdersItem.setOnAction(e -> openOrderWindow("viewOrders.fxml", "View Orders"));
        contextMenu.getItems().addAll(addOrderItem, viewOrdersItem);
        contextMenu.show(orderButton,
                orderButton.getScene().getWindow().getX() + orderButton.getLayoutX(),
                orderButton.getScene().getWindow().getY() + orderButton.getLayoutY() + orderButton.getHeight());
    }

    /**
     * Load giao diện Order (Add Order hoặc View Orders) vào vùng contentArea.
     */
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
