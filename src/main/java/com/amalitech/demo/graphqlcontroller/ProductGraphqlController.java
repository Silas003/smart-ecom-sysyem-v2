package com.amalitech.demo.graphqlcontroller;


import com.amalitech.demo.dto.request.ProductRequest;
import com.amalitech.demo.dto.response.ProductResponse;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@Tag(name = "GraphQL - Products", description = "GraphQL queries and mutations for products")
public class ProductGraphqlController {

    private final ProductService productService;

    public ProductGraphqlController(ProductService productService) {
        this.productService = productService;
    }

    @QueryMapping
    @Operation(summary = "List products (GraphQL)", description = "List products with pagination via GraphQL query")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class))))
    })
    public Page<Product> products(@Argument Integer page, @Argument Integer size) {
        return productService.getAllProducts(PageRequest.of(page,size));
    }

    @QueryMapping
    @Operation(summary = "Get product by id (GraphQL)", description = "Retrieve a single product by id via GraphQL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public Product productById(@Argument Long id) {
        return productService.getProductById(id);
    }

    @MutationMapping
    @Operation(summary = "Create product (GraphQL)", description = "Create a new product using a GraphQL mutation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public Product createProduct(@Argument("input")  ProductRequest request) {
        return productService.createProduct(request);
    }

    @MutationMapping
    @Operation(summary = "Update product (GraphQL)", description = "Update product by id using GraphQL mutation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public Product updateProduct(@Argument Long id, @Argument("input")  ProductRequest request) {
        return productService.updateProduct(id, request);
    }

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
}
