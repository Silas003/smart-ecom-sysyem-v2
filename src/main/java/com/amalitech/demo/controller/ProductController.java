package com.amalitech.demo.controller;


import com.amalitech.demo.models.Product;
import com.amalitech.demo.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/products")
public class ProductController {
    public ProductController(){}
    private static ProductService productService;
    public ProductController(ProductService productService){
        this.productService = productService;
    }
    @GetMapping("/")
    public ResponseEntity<List<Product>> getAllProducts(){
        List<Product> products = ProductService.getAllProducts();
        return  ResponseEntity.status(HttpStatus.OK).body(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id){
        Product product = ProductService.getProductById(id);
        return  ResponseEntity.status(HttpStatus.OK).body(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody @Valid Product product){
        Product updatedProduct = productService.updateProduct(id, product);
        return  ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/create_product")
    public ResponseEntity<Product> createProduct(@RequestBody @Valid Product product) {
        Product newProduct = ProductService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }
}
