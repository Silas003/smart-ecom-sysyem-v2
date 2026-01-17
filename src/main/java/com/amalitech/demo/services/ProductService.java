package com.amalitech.demo.services;

import com.amalitech.demo.dto.ProductRequest;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.ProductMapper;
import com.amalitech.demo.models.Category;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, CategoryService categoryService, ProductMapper productMapper){
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.productMapper = productMapper;
    }

    public Product createProduct(ProductRequest request) {
        if( productRepository.findByName(request.getName()) != null){
            throw new IllegalArgumentException("Product with given name already exists");
        }
        Category category = categoryService.getCategoryById(request.getCategoryId());
        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        return productRepository.save(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

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

    public void deleteProduct(Long id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("product not found"));

        productRepository.delete(existingProduct);
    }
}
