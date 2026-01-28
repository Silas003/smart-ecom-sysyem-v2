// ...existing code...
package com.amalitech.demo.dao.interfaces;

import com.amalitech.demo.models.Product;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductDao {
    Optional<Product> findById(Long id);
    Optional<Product> findByName(String name);
    boolean existsByName(String name);
    List<Product> findByCategoryId(Long categoryId, int limit, int offset);
    long countByCategoryId(Long categoryId);
    List<Product> findAll(int limit, int offset);
    long save(Product product);
    void update(Product product);
    void deleteById(Long id);
}
