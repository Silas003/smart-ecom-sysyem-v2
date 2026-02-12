package com.amalitech.demo.services;

import com.amalitech.demo.dto.request.CategoryRequest;
import com.amalitech.demo.dto.response.CategoryResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.CategoryMapper;
import com.amalitech.demo.models.Category;
import com.amalitech.demo.repository.CategoryRepository;
import com.amalitech.demo.services.interfaces.CategoryServiceInterface;
import com.amalitech.demo.utils.Sorter;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class CategoryService implements CategoryServiceInterface {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final Sorter<Category> sorter;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper, Sorter<Category> sorter) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.sorter = sorter;
    }

    @Cacheable(value = "category", key = "#id")
    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
        return categoryMapper.toResponse(category);
    }

    public Category getCategoryByIdForProduct(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
    }

    @Override
    @CachePut(value = "category", key = "#result.id")
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("category with given name already exists");
        }
        Category category = categoryMapper.toEntity(request);
        Category saved = categoryRepository.save(category);
        return categoryMapper.toResponse(saved);
    }

    @Cacheable(value = "allcategories")
    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> list = categoryRepository.findAll();
        if (list == null || list.isEmpty()) return List.of();
        List<Category> sorted = sorter.sort(list,
                Comparator.comparing(Category::getName, Comparator.nullsLast(String::compareToIgnoreCase)));
        return sorted.stream().map(categoryMapper::toResponse).toList();
    }

    @CachePut(value = "category", key = "#id")
    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("category not found"));

        existingCategory.setName(request.getName());

        Category saved = categoryRepository.save(existingCategory);
        return categoryMapper.toResponse(saved);
    }

    @Caching(evict = {
            @CacheEvict(value = "category", key = "#id", allEntries = true),
            @CacheEvict(value = "allcategories", allEntries = true)
    })
    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    public Category getProductCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
    }
}
