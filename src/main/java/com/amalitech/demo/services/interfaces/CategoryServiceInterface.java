package com.amalitech.demo.services.interfaces;

import com.amalitech.demo.dto.request.CategoryRequest;
import com.amalitech.demo.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryServiceInterface {
    CategoryResponse getCategoryById(Long id);

    CategoryResponse createCategory(CategoryRequest request);

    List<CategoryResponse> getAllCategories();

    CategoryResponse updateCategory(Long id, CategoryRequest request);

    void deleteCategory(Long id);
}
