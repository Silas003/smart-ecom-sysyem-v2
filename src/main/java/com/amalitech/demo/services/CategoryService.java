package com.amalitech.demo.services;

import com.amalitech.demo.dao.interfaces.CategoryDao;
import com.amalitech.demo.dto.request.CategoryRequest;
import com.amalitech.demo.dto.response.CategoryResponse;
import com.amalitech.demo.mapper.CategoryMapper;
import com.amalitech.demo.models.Category;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.services.interfaces.CategoryServiceInterface;
import com.amalitech.demo.utils.Sorter;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class CategoryService implements CategoryServiceInterface {
    private final CategoryDao categoryDao;
    private final CategoryMapper categoryMapper;
    private final Sorter<Category> sorter;

    public CategoryService(CategoryDao categoryDao, CategoryMapper categoryMapper, Sorter<Category> sorter) {
        this.categoryDao = categoryDao;
        this.categoryMapper = categoryMapper;
        this.sorter = sorter;
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryDao.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Category not found with id: " + id)
        );
        return  categoryMapper.toDto(category);
    }
    public Category getCategoryByIdForProduct(Long id) {
        return categoryDao.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Category not found with id: " + id)
        );

    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        if(categoryDao.findByName(request.getName()).isPresent()){
            throw new IllegalArgumentException("category with given name already exists");
        }
        Category category = categoryMapper.toEntity(request);
        categoryDao.save(category);
        return categoryMapper.toDto(category);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> list = categoryDao.findAll();
        if (list == null || list.isEmpty()) return List.of();
        List<Category> sorted = sorter.sort(list, Comparator.comparing(Category::getName, Comparator.nullsLast(String::compareToIgnoreCase)));
        return categoryMapper.toDto(sorted);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category existing = categoryDao.findById(id).orElseThrow(() -> new EntityNotFoundException("Category not found"));
        existing.setName(request.getName());
        categoryDao.update(existing);
        return categoryMapper.toDto(existing);
    }
    @Override
    public void deleteCategory(Long id) {
        categoryDao.deleteById(id);
    }
}
