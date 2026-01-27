package com.amalitech.demo.graphqlcontroller;


import com.amalitech.demo.dto.request.ProductRequest;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.services.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ProductGraphqlController {

    private final ProductService productService;

    public ProductGraphqlController(ProductService productService) {
        this.productService = productService;
    }

    @QueryMapping
    public Page<Product> products(@Argument Integer page, @Argument Integer size) {
        return productService.getAllProducts(PageRequest.of(page,size));
    }

    @QueryMapping
    public Product productById(@Argument Long id) {
        return productService.getProductById(id);
    }

    @MutationMapping
    public Product createProduct(@Argument("input")  ProductRequest request) {
        return productService.createProduct(request);
    }

    @MutationMapping
    public Product updateProduct(@Argument Long id, @Argument("input")  ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @MutationMapping
    public Boolean deleteProduct(@Argument Long id) {
        productService.deleteProduct(id);
        return true;
    }
}
