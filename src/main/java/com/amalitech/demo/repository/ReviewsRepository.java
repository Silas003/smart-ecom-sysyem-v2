package com.amalitech.demo.repository;

import com.amalitech.demo.models.Reviews;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewsRepository extends JpaRepository<Reviews, Long>, JpaSpecificationExecutor<Reviews> {
    @EntityGraph(value="reviews-with-product", type = EntityGraph.EntityGraphType.FETCH)
    List<Reviews> findByProduct_IdOrderByIdDesc(Long productId);

    @EntityGraph(value="reviews-with-user", type = EntityGraph.EntityGraphType.FETCH)
    List<Reviews> findByUser_IdOrderByIdDesc(Long userId);
}
