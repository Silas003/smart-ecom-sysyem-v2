package com.amalitech.demo.dao.interfaces;

import com.amalitech.demo.models.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryDao {
    Optional<Category> findById(Long id);
    Optional<Category> findByName(String name);
    List<Category> findAll();
    long save(Category category);
    void update(Category category);
    void deleteById(Long id);
}
