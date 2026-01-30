package com.amalitech.demo.restcontroller;


import com.amalitech.demo.dto.request.ProductRequest;
import com.amalitech.demo.dto.response.ProductResponse;
import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.mapper.ProductMapper;
import com.amalitech.demo.services.interfaces.ProductServiceInterface;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping(value = "/api/v1/products")
@AllArgsConstructor
@Tag(name = "Products", description = "APIs to manage products")
public class ProductController {
    private final ProductServiceInterface productService;
    private final ProductMapper productMapper;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "List products", description = "List products with pagination and sorting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class))))
    })

//    todo:work on price filtering
    public ResponseDto<Page<ProductResponse>> getAllProducts(
            @PageableDefault(size = 10, sort = "price", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false,defaultValue = "0") Integer minPrice,
            @RequestParam(required = false,defaultValue = "0") Integer maxPrice
    ) {
        Page<Product> products = productService.getAllProducts(pageable);
        Page<ProductResponse> resp = products.map(productMapper::toResponse);
        return new ResponseDto<>(HttpStatus.OK, "products retrieved", resp);


    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get product", description = "Retrieve a single product by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseDto<ProductResponse> getProductById(@Parameter(description = "ID of the product to retrieve", required = true) @PathVariable Long id) {
        Product product = productService.getProductById(id);
        return new ResponseDto<>(HttpStatus.OK, "product retrieved", productMapper.toResponse(product));

    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update product", description = "Update product details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseDto<ProductResponse> updateProduct(@Parameter(description = "ID of the product to update", required = true) @PathVariable Long id, @RequestBody @Valid ProductRequest productRequest) {
        Product updated = productService.updateProduct(id, productRequest);
        return new ResponseDto<>(HttpStatus.OK, "product updated", productMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete product", description = "Delete a product by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> deleteProduct(@Parameter(description = "ID of the product to delete", required = true) @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create product", description = "Create a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })

    public ResponseDto<ProductResponse> createProduct(@RequestBody @Valid ProductRequest productRequest) {
        Product newProduct = productService.createProduct(productRequest);
        return new ResponseDto<>(HttpStatus.CREATED, "product created ", productMapper.toResponse(newProduct));

    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category", description = "Retrieve products belonging to a specific category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseDto<Page<ProductResponse>> getProductsByCategory(
            @Parameter(description = "ID of the category", required = true) @PathVariable Long categoryId,
            @PageableDefault(size = 10, sort = "price", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<ProductResponse> products = productService.getProductsByCategoryId(categoryId);
        return new ResponseDto<>(HttpStatus.OK, "products retrieved", products);
    }
}