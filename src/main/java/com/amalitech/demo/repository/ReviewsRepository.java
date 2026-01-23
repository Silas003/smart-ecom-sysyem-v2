package com.amalitech.demo.repository;

import com.amalitech.demo.models.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewsRepository extends JpaRepository<Reviews, Long> {
    List<Reviews> findByProduct_IdOrderByIdDesc(Long productId);
    List<Reviews> findByUser_IdOrderByIdDesc(Long userId);
}
