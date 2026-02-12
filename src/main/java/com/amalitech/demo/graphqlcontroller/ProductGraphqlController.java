package com.amalitech.demo.graphqlcontroller;


import com.amalitech.demo.dto.request.ProductRequest;
import com.amalitech.demo.dto.response.ProductPageResponse;
import com.amalitech.demo.dto.response.ProductResponse;
import com.amalitech.demo.mapper.ProductMapper;
import com.amalitech.demo.models.Category;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.services.CategoryService;
import com.amalitech.demo.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Tag(name = "GraphQL - Products", description = "GraphQL queries and mutations for products")
public class ProductGraphqlController {

        private final ProductService productService;
        private final ProductMapper productMapper;
        private final CategoryService categoryService;

        public ProductGraphqlController(ProductService productService, ProductMapper productMapper, CategoryService categoryService) {
                this.categoryService = categoryService;
                this.productService = productService;
                this.productMapper = productMapper;
        }

        // Public catalog queries
    @QueryMapping
    @Operation(summary = "List products (GraphQL)", description = "List products with pagination via GraphQL query")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class))))
    })
        public ProductPageResponse products(@Argument Integer page, @Argument Integer size,@RequestParam(required = false) Long categoryId) {
                Page<Product> p = productService.getAllProducts(PageRequest.of(page,size),categoryId);
                var items = p.getContent().stream().map(productMapper::toResponse).toList();
                return new ProductPageResponse(items, p.getTotalElements());
        }

    @QueryMapping
    @Operation(summary = "Get product by id (GraphQL)", description = "Retrieve a single product by id via GraphQL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
        public ProductResponse productById(@Argument Long id) {
                return productMapper.toResponse(productService.getProductById(id));
        }

        // Admin/Seller mutations
        @PreAuthorize("hasAnyRole('admin','seller')")
    @MutationMapping
    @Operation(summary = "Create product (GraphQL)", description = "Create a new product using a GraphQL mutation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
        public ProductResponse createProduct(@Argument("input")  ProductRequest request) {
                return productMapper.toResponse(productService.createProduct(request));
        }

    @PreAuthorize("hasAnyRole('admin','seller')")
    @MutationMapping
    @Operation(summary = "Update product (GraphQL)", description = "Update product by id using GraphQL mutation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
        public ProductResponse updateProduct(@Argument Long id, @Argument("input")  ProductRequest request) {
                return productMapper.toResponse(productService.updateProduct(id, request));
        }

    @PreAuthorize("hasAnyRole('admin','seller')")
    @MutationMapping
    @Operation(summary = "Delete product (GraphQL)", description = "Delete a product by id using GraphQL mutation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deleted"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
        public Boolean deleteProduct(@Argument Long id) {
                productService.deleteProduct(id);
                return true;
        }

    @SchemaMapping(typeName = "Product", field = "category")
    public Category category(Product product) {
        return categoryService.getCategoryByIdForProduct(product.getCategory().getId());
    }
}
