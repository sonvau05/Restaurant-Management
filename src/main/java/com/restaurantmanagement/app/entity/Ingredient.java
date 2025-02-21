package com.restaurantmanagement.app.entity;

import javafx.beans.property.*;

public class Ingredient {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty category;
    private final StringProperty unit;
    private final DoubleProperty stock;
    private final DoubleProperty minStock;
    private final DoubleProperty pricePerUnit;

    public Ingredient(int id, String name, String category, String unit, double stock, double minStock, double pricePerUnit) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.category = new SimpleStringProperty(category);
        this.unit = new SimpleStringProperty(unit);
        this.stock = new SimpleDoubleProperty(stock);
        this.minStock = new SimpleDoubleProperty(minStock);
        this.pricePerUnit = new SimpleDoubleProperty(pricePerUnit);
    }

    public Ingredient(String name, String unit) {
        this.id = new SimpleIntegerProperty(0);
        this.name = new SimpleStringProperty(name);
        this.category = new SimpleStringProperty("");
        this.unit = new SimpleStringProperty(unit);
        this.stock = new SimpleDoubleProperty(0.0);
        this.minStock = new SimpleDoubleProperty(0.0);
        this.pricePerUnit = new SimpleDoubleProperty(0.0);
    }

    // Getter
    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getCategory() { return category.get(); }
    public String getUnit() { return unit.get(); }
    public double getStock() { return stock.get(); }
    public double getMinStock() { return minStock.get(); }
    public double getPricePerUnit() { return pricePerUnit.get(); }

    // Setter (Thêm setter để sửa lỗi)
    public void setId(int id) { this.id.set(id); }
    public void setName(String name) { this.name.set(name); }  // 🔹 Thêm setName()
    public void setCategory(String category) { this.category.set(category); }
    public void setUnit(String unit) { this.unit.set(unit); }
    public void setStock(double stock) { this.stock.set(stock); }
    public void setMinStock(double minStock) { this.minStock.set(minStock); }
    public void setPricePerUnit(double pricePerUnit) { this.pricePerUnit.set(pricePerUnit); }

    // Property methods (dùng cho JavaFX binding)
    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty categoryProperty() { return category; }
    public StringProperty unitProperty() { return unit; }
    public DoubleProperty stockProperty() { return stock; }
    public DoubleProperty minStockProperty() { return minStock; }
    public DoubleProperty pricePerUnitProperty() { return pricePerUnit; }
}
