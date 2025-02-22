package com.restaurantmanagement.app.service;

import com.restaurantmanagement.app.entity.Category;
import com.restaurantmanagement.app.entity.MenuItems;
import com.restaurantmanagement.app.repository.CategoryRepository;

import com.restaurantmanagement.app.repository.MenuRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MenuService {
    private MenuRepository managerMenuRepository;
    private CategoryRepository categoryRepository;

    public MenuService() {
        managerMenuRepository = new MenuRepository();
        categoryRepository = new CategoryRepository();
    }

    public ObservableList<MenuItems> getAllMenuItems() {
        return FXCollections.observableArrayList(managerMenuRepository.getAllMenuItems());
    }

    public ObservableList<Category> getAllCategories() {
        return FXCollections.observableArrayList(categoryRepository.getAllCategories());
    }

    public void addMenuItem(String name, double price, String description, int categoryId) {
        managerMenuRepository.addMenuItem(name, price, description, categoryId);
    }

    public void updateMenuItem(int id, String name, double price, String description, int categoryId) {
        managerMenuRepository.updateMenuItem(id, name, price, description, categoryId);
    }

    public ObservableList<MenuItems> searchMenuItems(String searchQuery) {
        return FXCollections.observableArrayList(managerMenuRepository.searchMenuItems(searchQuery));
    }

    public void deleteMenuItems(int id) {
        managerMenuRepository.deleteMenuItem(id);
    }
}
