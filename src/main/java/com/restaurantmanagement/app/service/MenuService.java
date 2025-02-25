package com.restaurantmanagement.app.service;

import com.restaurantmanagement.app.entity.Category;
import com.restaurantmanagement.app.entity.MenuItem;
import com.restaurantmanagement.app.repository.CategoryRepository;
import com.restaurantmanagement.app.repository.MenuRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MenuService {
    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;

    public MenuService() {
        this.menuRepository = new MenuRepository();
        this.categoryRepository = new CategoryRepository();
    }

    public List<MenuItem> getAllMenuItems() {
        return menuRepository.getAllMenuItems();
    }

    public MenuItem getMenuItemByName(String name) {
        List<MenuItem> menuItems = menuRepository.getAllMenuItems();
        for (MenuItem item : menuItems) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.getAllCategories();
    }

    public void addMenuItem(String name, double price, String description, int categoryId) {
        menuRepository.addMenuItem(name, price, description, categoryId);
    }

    public void updateMenuItem(int id, String name, double price, String description, int categoryId) {
        menuRepository.updateMenuItem(id, name, price, description, categoryId);
    }

    public List<MenuItem> searchMenuItems(String keyword) {
        return menuRepository.searchMenuItems(keyword);
    }

    public void deleteMenuItem(int id) {
        menuRepository.deleteMenuItem(id);
    }
}