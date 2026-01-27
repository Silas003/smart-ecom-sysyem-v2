package com.amalitech.demo.services.interfaces;

import com.amalitech.demo.dto.request.CategoryRequest;
import com.amalitech.demo.models.Category;

import java.util.List;

public interface CategoryServiceInterface {
    Category getCategoryById(Long id);

    Category createCategory(CategoryRequest request);

    List<Category> getAllCategories();

    Category updateCategory(Long id, CategoryRequest request);

    void deleteCategory(Long id);
}
