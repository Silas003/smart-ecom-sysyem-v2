package com.amalitech.demo.services;

import com.amalitech.demo.dto.request.CategoryRequest;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.CategoryMapper;
import com.amalitech.demo.models.Category;
import com.amalitech.demo.repository.CategoryRepository;
import com.amalitech.demo.services.interfaces.CategoryServiceInterface;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService implements CategoryServiceInterface {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }
    @Cacheable(value="category",key="#id")
    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Category not found with id: " + id)
        );
    }

    @Override
    @CachePut(value="category",key ="result.id")
    public Category createCategory(CategoryRequest request) {
        if(categoryRepository.findByName(request.getName()) != null){
            throw new IllegalArgumentException("category with given name already exists");
        }
        Category category = categoryMapper.toEntity(request);
        return categoryRepository.save(category);
    }

    @Cacheable(value = "allcategories")
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }


    @CachePut(value = "category",key = "#id")
    @Override
    public Category updateCategory(Long id, CategoryRequest request) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("category not found"));

        existingCategory.setName(request.getName());

        return categoryRepository.save(existingCategory);
    }

    @Caching(
            evict ={
                    @CacheEvict(value = "category",key = "#id",allEntries = true),
                    @CacheEvict(value = "allcategories",allEntries = true)

            }
    )
    @Override
    public void deleteCategory(Long id) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("category not found"));

        categoryRepository.delete(existingCategory);
    }
}