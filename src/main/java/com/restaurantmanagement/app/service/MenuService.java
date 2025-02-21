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
        // Trả về danh sách tất cả các món ăn từ cơ sở dữ liệu
        return FXCollections.observableArrayList(managerMenuRepository.getAllMenuItems());
    }

    public ObservableList<Category> getAllCategories() {
        // Trả về danh sách tất cả các danh mục từ cơ sở dữ liệu
        return FXCollections.observableArrayList(categoryRepository.getAllCategories());
    }

    public void addMenuItem(String name, double price, String description, int categoryId) {
        // Thêm món ăn mới vào cơ sở dữ liệu
        managerMenuRepository.addMenuItem(name, price, description, categoryId);
    }

    public void updateMenuItem(int id, String name, double price, String description, int categoryId) {
        // Cập nhật món ăn trong cơ sở dữ liệu
        managerMenuRepository.updateMenuItem(id, name, price, description, categoryId);
    }

    // Phương thức tìm kiếm món ăn theo tên
    public ObservableList<MenuItems> searchMenuItems(String searchQuery) {
        // Trả về danh sách món ăn tìm được dựa trên tên
        return FXCollections.observableArrayList(managerMenuRepository.searchMenuItems(searchQuery));
    }

    // Phương thức xóa món ăn
    public void deleteMenuItems(int id) {
        // Xóa món ăn từ cơ sở dữ liệu
        managerMenuRepository.deleteMenuItem(id);
    }
}
