package com.amalitech.demo.services;

import com.amalitech.demo.dto.CategoryRequest;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.CategoryMapper;
import com.amalitech.demo.models.Category;
import com.amalitech.demo.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public  Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Category not found with id: " + id)
        );
    }

    public  Category createCategory(CategoryRequest request) {
        if(categoryRepository.findByName(request.getName()) != null){
            throw new IllegalArgumentException("category with given name already exists");
        }
        Category category = categoryMapper.toEntity(request);
        return categoryRepository.save(category);
    }


    public  List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category updateCategory(Long id, CategoryRequest request) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("category not found"));

        existingCategory.setName(request.getName());

        return categoryRepository.save(existingCategory);
    }
    public void deleteCategory(Long id) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("category not found"));

        categoryRepository.delete(existingCategory);
    }
}
