package com.amalitech.demo.services;


import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.models.Category;
import com.amalitech.demo.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public  Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Category not found with id: " + id)
        );
    }

    public  Category createCategory(Category category) {
        if(categoryRepository.findByName(category.getName()) != null){
            throw new IllegalArgumentException("category with given name already exists");
        }
        return categoryRepository.save(category);
    }


    public  List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category updateCategory(Long id, Category category) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("category not found"));

        existingCategory.setName(category.getName());

        return categoryRepository.save(existingCategory);
    }
    public void deleteCategory(Long id) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("category not found"));

        categoryRepository.delete(existingCategory);
    }
}
