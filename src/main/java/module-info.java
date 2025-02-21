module com.restaurantmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.sql;
    requires java.desktop;

    exports com.restaurantmanagement.app;
    exports com.restaurantmanagement.app.controller;

    opens com.restaurantmanagement.app.entity to javafx.base;
    opens com.restaurantmanagement.app.controller to javafx.fxml;
}
