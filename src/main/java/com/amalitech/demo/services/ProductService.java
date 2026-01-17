package com.amalitech.demo.services;

import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {


    private final ProductRepository productRepository ;
    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public  Product createProduct(Product product) {
        if( productRepository.findByName(product.getName()) != null){
            throw new IllegalArgumentException("Product with given name already exists");
        }
        return productRepository.save(product);
    }
    public  Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product updateProduct(Long id, Product product) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setStockQuantity(product.getStockQuantity());
        existingProduct.setCategory(product.getCategory());

        return productRepository.save(existingProduct);
    }
}
