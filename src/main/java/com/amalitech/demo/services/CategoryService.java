package com.amalitech.demo.services;

import com.amalitech.demo.dto.request.CategoryRequest;
import com.amalitech.demo.dto.response.CategoryResponse;
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
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Category not found with id: " + id)
        );
        return categoryMapper.toResponse(category);
    }

    @Override
    @CachePut(value="category",key ="result.id")
    public CategoryResponse createCategory(CategoryRequest request) {
        if(categoryRepository.findByName(request.getName()) != null){
            throw new IllegalArgumentException("category with given name already exists");
        }
        Category category = categoryMapper.toEntity(request);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Cacheable(value = "allcategories")
    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return  categories.stream().map(categoryMapper::toResponse).toList();
    }


    @CachePut(value = "category",key = "#id")
    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("category not found"));

        existingCategory.setName(request.getName());

        return categoryMapper.toResponse(categoryRepository.save(existingCategory));
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

    public Category getProductCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Category not found with id: " + id)
        );
    }
}