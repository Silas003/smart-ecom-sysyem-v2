package com.amalitech.demo.services;

import com.amalitech.demo.dto.request.ProductRequest;
import com.amalitech.demo.dto.response.ProductResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.ProductMapper;
import com.amalitech.demo.models.Category;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.repository.ProductRepository;
import com.amalitech.demo.services.interfaces.ProductServiceInterface;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductService implements ProductServiceInterface {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, CategoryService categoryService, ProductMapper productMapper){
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.productMapper = productMapper;
    }

    @Override
    @CachePut(value="productsByCategory", key="#request.categoryId")
    public Product createProduct(ProductRequest request) {
        if( productRepository.findByName(request.getName()) != null){
            throw new IllegalArgumentException("Product with given name already exists");
        }
        Category category = categoryService.getCategoryById(request.getCategoryId());
        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        return productRepository.save(product);
    }

    @Override
    @Cacheable(value="product", key="#id",sync = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    @Caching(
            evict ={
                    @CacheEvict(value = "productsByCategory", allEntries = true),
                    @CacheEvict(value = "product", key = "#id")
            }
    )
    public Product updateProduct(Long id, ProductRequest request) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        existingProduct.setName(request.getName());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setStockQuantity(request.getStockQuantity());
        if (existingProduct.getCategory() == null || !existingProduct.getCategory().getId().equals(request.getCategoryId())){
            Category newCat = categoryService.getCategoryById(request.getCategoryId());
            existingProduct.setCategory(newCat);
        }

        return productRepository.save(existingProduct);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "productsByCategory", allEntries = true),
                    @CacheEvict(value = "product", key = "#id")
            }
    )
    @Override
    public void deleteProduct(Long id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("product not found"));

        productRepository.delete(existingProduct);
    }

    @Cacheable(value="productsByCategory", key="#categoryId",sync = true)
    @Override
    public Page<ProductResponse> getProductsByCategoryId(Long categoryId){
        Category category = categoryService.getCategoryById(categoryId);
        Page<Product> products = productRepository.findByCategory_Id(categoryId,Pageable.unpaged());
        return products.map(productMapper::toResponse);
    }

}
