package com.restaurantmanagement.app.entity;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Category {
    private IntegerProperty categoryID;
    private StringProperty name;

    public Category(int categoryID, String name) {
        this.categoryID = new SimpleIntegerProperty(categoryID);
        this.name = new SimpleStringProperty(name);
    }

    public int getCategoryID() {
        return categoryID.get();
    }

    public void setCategoryID(int categoryID) {
        this.categoryID.set(categoryID);
    }

    public IntegerProperty categoryIDProperty() {
        return categoryID;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
