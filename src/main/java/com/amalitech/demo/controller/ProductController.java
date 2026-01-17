package com.amalitech.demo.controller;


import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/api/v1/products")
@Tag(name = "Products", description = "APIs to manage products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService){

        this.productService = productService;
    }
    @GetMapping("/")
    @Operation(summary = "List products", description = "List products with pagination and sorting")
    public ResponseDto getAllProducts(
            @PageableDefault(size = 10, sort = "price", direction = Sort.Direction.ASC) Pageable pageable
    )
    {
        Page<Product> products = productService.getAllProducts(pageable);
        ResponseDto responseDto = new ResponseDto(HttpStatus.OK,"products retrieved",products);

        return  responseDto;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product", description = "Retrieve a single product by id")
    public ResponseEntity<Product> getProductById(@PathVariable Long id){
        Product product = productService.getProductById(id);
        return  ResponseEntity.status(HttpStatus.OK).body(product);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update product details")
    public ResponseEntity<ResponseDto> updateProduct(@PathVariable Long id, @RequestBody @Valid Product product){
        Product updatedProduct = productService.updateProduct(id, product);
        ResponseDto responseDto = new ResponseDto(HttpStatus.OK,"product updated",updatedProduct);

        return  ResponseEntity.status(HttpStatus.ACCEPTED).body(responseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Delete a product by id")
    public ResponseEntity<ResponseDto> deleteProduct(@PathVariable Long id) {
        ResponseDto responseDto = new ResponseDto(HttpStatus.OK,"products retrieved",null);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(responseDto);
    }

    @PostMapping("/create_product")
    @Operation(summary = "Create product", description = "Create a new product")
    public ResponseEntity<Product> createProduct(@RequestBody @Valid Product product) {
        Product newProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }
}
