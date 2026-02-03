// ...existing code...
package com.amalitech.demo.dao.interfaces;

import com.amalitech.demo.models.Category;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryDao {
    Optional<Category> findById(Long id);
    Optional<Category> findByName(String name);
    boolean existsByName(String name);
    List<Category> findAll();
    long save(Category category);
    void update(Category category);
    void deleteById(Long id);
}
