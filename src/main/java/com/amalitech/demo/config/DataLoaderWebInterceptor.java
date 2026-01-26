package com.amalitech.demo.config;

import com.amalitech.demo.models.Product;
import com.amalitech.demo.services.ProductService;
import org.dataloader.BatchLoader;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Configuration
public class DataLoaderWebInterceptor implements WebGraphQlInterceptor {

    private final ProductService productService;

    public DataLoaderWebInterceptor(ProductService productService) {
        this.productService = productService;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        DataLoaderRegistry registry = new DataLoaderRegistry();

        BatchLoader<Long, Product> productBatchLoader = new BatchLoader<Long, Product>() {
            @Override
            public CompletableFuture<List<Product>> load(List<Long> keys) {
                return CompletableFuture.supplyAsync(() -> productService.loadProductsByIds(keys));
            }
        };

        DataLoader<Long, Product> productLoader = DataLoader.newDataLoader(productBatchLoader);
        registry.register("productLoader", productLoader);

        request.configureExecutionInput((executionInput) -> executionInput.transform(builder -> builder.dataLoaderRegistry(registry)));

        return chain.next(request);
    }
}
