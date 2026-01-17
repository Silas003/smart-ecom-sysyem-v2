package com.amalitech.demo.controller;


import com.amalitech.demo.dto.ProductRequest;
import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.models.Category;
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

@RestController
@RequestMapping(value = "/api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }
    @GetMapping("/")
    public ResponseDto getAllProducts(
            @PageableDefault(size = 10, sort = "price", direction = Sort.Direction.ASC) Pageable pageable
    )
    {
        Page<Product> products = productService.getAllProducts(pageable);
        ResponseDto responseDto = new ResponseDto(HttpStatus.OK,"products retrieved",products);

        return  responseDto;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> getProductById(@PathVariable Long id){
        Product product = productService.getProductById(id);
        ResponseDto responseDto = new ResponseDto(HttpStatus.OK,"product retrieved",product);

        return  ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductRequest productRequest){
        Product updatedProduct = productService.updateProduct(id, productRequest);
        ResponseDto responseDto = new ResponseDto(HttpStatus.ACCEPTED,"product updated ",updatedProduct);

        return  ResponseEntity.status(HttpStatus.ACCEPTED).body(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    @PostMapping("/create_product")
    public ResponseEntity<ResponseDto> createProduct(@RequestBody @Valid ProductRequest productRequest) {
        Product newProduct = productService.createProduct(productRequest);
        ResponseDto responseDto = new ResponseDto(HttpStatus.CREATED,"product created ",newProduct);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
