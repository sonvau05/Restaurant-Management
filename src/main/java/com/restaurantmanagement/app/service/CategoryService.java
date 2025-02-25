package com.restaurantmanagement.app.service;

import com.restaurantmanagement.app.entity.Category;
import com.restaurantmanagement.app.repository.CategoryRepository;

import java.util.List;

public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService() {
        this.categoryRepository = new CategoryRepository();
    }

    public List<Category> getAllCategories() {
        return categoryRepository.getAllCategories();
    }

    public void addCategory(Category category) {
        categoryRepository.addCategory(category);
    }

    public void updateCategory(Category category) {
        categoryRepository.updateCategory(category);
    }

    public void deleteCategory(int categoryID) {
        categoryRepository.deleteCategory(categoryID);
    }
}