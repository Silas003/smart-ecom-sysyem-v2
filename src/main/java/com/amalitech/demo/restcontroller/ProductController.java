package com.amalitech.demo.restcontroller;


import com.amalitech.demo.dto.ProductRequest;
import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.services.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Tag(name = "Products", description = "APIs to manage products")
public class ProductController {
    private final ProductService productService;

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "List products", description = "List products with pagination and sorting")
    public ResponseDto<Page<Product>> getAllProducts(
            @PageableDefault(size = 10, sort = "price", direction = Sort.Direction.ASC) Pageable pageable
    )
    {
        Page<Product> products = productService.getAllProducts(pageable);
        return new ResponseDto<>(HttpStatus.OK,"products retrieved",products);


    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get product", description = "Retrieve a single product by id")
    public ResponseDto<Product> getProductById(@PathVariable Long id){
        Product product = productService.getProductById(id);
        return new ResponseDto<>(HttpStatus.OK,"product retrieved",product);

    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update product", description = "Update product details")
    public ResponseDto<Product> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductRequest productRequest){
        Product updatedProduct = productService.updateProduct(id, productRequest);
        return  new ResponseDto<>(HttpStatus.OK,"product updated ",updatedProduct);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete product", description = "Delete a product by id")

    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    @PostMapping("/create_product")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create product", description = "Create a new product")

    public ResponseDto<Product> createProduct(@RequestBody @Valid ProductRequest productRequest) {
        Product newProduct = productService.createProduct(productRequest);
        return new ResponseDto<>(HttpStatus.CREATED,"product created ",newProduct);

    }
}
