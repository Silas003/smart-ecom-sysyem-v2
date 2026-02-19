package com.amalitech.demo.repository;

import com.amalitech.demo.models.Product;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    boolean existsByName( String name);

    Page<Product> findByCategory_Id(@NotNull Long categoryId, Pageable unpaged);

    @EntityGraph(value = "product-with-category", type = EntityGraph.EntityGraphType.FETCH)
    Page<Product> findAll(Pageable pageable);

    @Query("SELECT p from Product p JOIN p.category c WHERE c.name = :categoryName")
    Page<Product> findByCategory_Name(String categoryName, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity < :threshold")
    Page<Product> findLowStockProducts(@Param("threshold") int threshold, Pageable pageable);
}
