package com.amalitech.demo.services;

import com.amalitech.demo.dao.interfaces.CategoryDao;
import com.amalitech.demo.dto.request.CategoryRequest;
import com.amalitech.demo.mapper.CategoryMapper;
import com.amalitech.demo.models.Category;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.services.interfaces.CategoryServiceInterface;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService implements CategoryServiceInterface {
    private final CategoryDao categoryDao;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryDao categoryDao, CategoryMapper categoryMapper) {
        this.categoryDao = categoryDao;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryDao.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Category not found with id: " + id)
        );
    }

    @Override
    public Category createCategory(CategoryRequest request) {
        if(categoryDao.findByName(request.getName()).isPresent()){
            throw new IllegalArgumentException("category with given name already exists");
        }
        Category category = categoryMapper.toEntity(request);
        categoryDao.save(category);
        return category;
    }


    @Override
    public List<Category> getAllCategories() {
        return categoryDao.findAll();
    }

    @Override
    public Category updateCategory(Long id, CategoryRequest request) {
        Category existing = categoryDao.findById(id).orElseThrow(() -> new EntityNotFoundException("Category not found"));
        existing.setName(request.getName());
        categoryDao.update(existing);
        return existing;
    }
    @Override
    public void deleteCategory(Long id) {
        categoryDao.deleteById(id);
    }
}
