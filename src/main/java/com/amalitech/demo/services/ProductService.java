package com.amalitech.demo.services;

import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {


    private static ProductRepository productRepository ;
    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public static Product createProduct(Product product) {
        if( productRepository.findByName(product.getName()) != null){
            throw new IllegalArgumentException("Product with given name already exists");
        }
        return productRepository.save(product);
    }
    public static Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    public static List<Product> getAllProducts() {
        return productRepository.findAll();
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
