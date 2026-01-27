package com.amalitech.demo.services.interfaces;

import com.amalitech.demo.dto.request.ProductRequest;
import com.amalitech.demo.dto.response.ProductResponse;
import com.amalitech.demo.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductServiceInterface {
    Product createProduct(ProductRequest request);

    Product getProductById(Long id);

    Page<Product> getAllProducts(Pageable pageable);

    Product updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);

    Page<ProductResponse> getProductsByCategoryId(Long categoryId);
}
