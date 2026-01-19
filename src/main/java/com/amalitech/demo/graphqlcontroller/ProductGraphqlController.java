package com.amalitech.demo.graphqlcontroller;

import com.amalitech.demo.dto.ProductInput;
import com.amalitech.demo.dto.ProductRequest;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.services.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProductGraphqlController {

    private final ProductService productService;

    public ProductGraphqlController(ProductService productService) {
        this.productService = productService;
    }

    @QueryMapping
    public List<Product> products() {
        return productService.getAllProducts(Pageable.unpaged()).getContent();
    }

    @QueryMapping
    public Product productById(@Argument Long id) {
        return productService.getProductById(id);
    }

    @MutationMapping
    public Product createProduct(@Argument ProductInput input) {
        ProductRequest req = new ProductRequest();
        req.setName(input.getName());
        req.setPrice(input.getPrice());
        req.setStockQuantity(input.getStockQuantity());
        req.setCategoryId(input.getCategoryId());
        return productService.createProduct(req);
    }

    @MutationMapping
    public Product updateProduct(@Argument Long id, @Argument ProductInput input) {
        ProductRequest req = new ProductRequest();
        req.setName(input.getName());
        req.setPrice(input.getPrice());
        req.setStockQuantity(input.getStockQuantity());
        req.setCategoryId(input.getCategoryId());
        return productService.updateProduct(id, req);
    }

    @MutationMapping
    public Boolean deleteProduct(@Argument Long id) {
        productService.deleteProduct(id);
        return true;
    }
}
