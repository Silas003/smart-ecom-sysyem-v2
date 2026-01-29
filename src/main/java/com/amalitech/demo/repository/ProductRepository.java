package com.amalitech.demo.repository;

import com.amalitech.demo.models.Product;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByName( String name);
    boolean existsByName( String name);

    Page<Product> findByCategory_Id(@NotNull Long categoryId, Pageable unpaged);

    @EntityGraph(value = "product-with-category", type = EntityGraph.EntityGraphType.FETCH)
    Page<Product> findAll(Pageable pageable);
}
