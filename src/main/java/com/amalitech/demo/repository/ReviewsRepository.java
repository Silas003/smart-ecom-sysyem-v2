package com.amalitech.demo.repository;

import com.amalitech.demo.models.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewsRepository extends JpaRepository<Reviews, Long>, JpaSpecificationExecutor<Reviews> {

    @Query("SELECT AVG(r.rating) FROM Reviews r WHERE r.product.id = :productId")
    Double getAverageRatingByProductId(@Param("productId") Long productId);
}
