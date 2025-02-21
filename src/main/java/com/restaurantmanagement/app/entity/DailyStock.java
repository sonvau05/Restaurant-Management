package com.restaurantmanagement.app.entity;

import javafx.beans.property.*;

public class DailyStock {

    private final IntegerProperty stockID;
    private final IntegerProperty ingredientID;
    private final StringProperty name;
    private final IntegerProperty categoryID;
    private final StringProperty unit;
    private final DoubleProperty stock;
    private final DoubleProperty minStock;
    private final DoubleProperty pricePerUnit;
    private final StringProperty date;

    public DailyStock(int stockID, int ingredientID, String name, int categoryID, String unit, double stock, double minStock, double pricePerUnit, String date) {
        this.stockID = new SimpleIntegerProperty(stockID);
        this.ingredientID = new SimpleIntegerProperty(ingredientID);
        this.name = new SimpleStringProperty(name);
        this.categoryID = new SimpleIntegerProperty(categoryID);
        this.unit = new SimpleStringProperty(unit);
        this.stock = new SimpleDoubleProperty(stock);
        this.minStock = new SimpleDoubleProperty(minStock);
        this.pricePerUnit = new SimpleDoubleProperty(pricePerUnit);
        this.date = new SimpleStringProperty(date);
    }

    public IntegerProperty stockIDProperty() {
        return stockID;
    }

    public IntegerProperty ingredientIDProperty() {
        return ingredientID;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public IntegerProperty categoryIDProperty() {
        return categoryID;
    }

    public StringProperty unitProperty() {
        return unit;
    }

    public DoubleProperty stockProperty() {
        return stock;
    }

    public DoubleProperty minStockProperty() {
        return minStock;
    }

    public DoubleProperty pricePerUnitProperty() {
        return pricePerUnit;
    }

    public int getStockID() {
        return stockID.get();
    }

    public int getIngredientID() {
        return ingredientID.get();
    }

    public String getName() {
        return name.get();
    }

    public int getCategoryID() {
        return categoryID.get();
    }

    public String getUnit() {
        return unit.get();
    }

    public double getStock() {
        return stock.get();
    }

    public double getMinStock() {
        return minStock.get();
    }

    public double getPricePerUnit() {
        return pricePerUnit.get();
    }

    public String getDate() {
        return date.get();
    }

    public StringProperty dateProperty() {
        return date;
    }
}
