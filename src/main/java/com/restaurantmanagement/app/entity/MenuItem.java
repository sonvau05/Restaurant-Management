package com.restaurantmanagement.app.entity;

import java.math.BigDecimal;

public class MenuItem {
    private int itemID;
    private String name;
    private int categoryID;
    private BigDecimal price;
    private String description;

    public MenuItem(int itemID, String name, int categoryID, BigDecimal price, String description) {
        this.itemID = itemID;
        this.name = name;
        this.categoryID = categoryID;
        this.price = price;
        this.description = description;
    }

    public int getItemID() { return itemID; }
    public String getName() { return name; }
    public int getCategoryID() { return categoryID; }
    public BigDecimal getPrice() { return price; }
    public String getDescription() { return description; }
}