package com.amalitech.demo.dao.interfaces;

import com.amalitech.demo.models.Reviews;

import java.util.List;
import java.util.Optional;


public interface ReviewsDao {
    Optional<Reviews> findById(Long id);
    List<Reviews> findAll();
    List<Reviews> findByProductIdOrderByIdDesc(Long productId);
    List<Reviews> findByUserIdOrderByIdDesc(Long userId);
    long save(Reviews reviews);
    void deleteById(Long id);
}
