package com.amalitech.demo.services;

import com.amalitech.demo.dto.request.ProductRequest;
import com.amalitech.demo.dto.response.ProductResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.ProductMapper;
import com.amalitech.demo.models.Category;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.repository.ProductRepository;
import com.amalitech.demo.services.interfaces.ProductServiceInterface;
import com.amalitech.demo.utils.Sorter;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.amalitech.demo.services.specification.ProductSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ProductService implements ProductServiceInterface {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, CategoryService categoryService, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.productMapper = productMapper;
    }

    @Override
    @CachePut(value = "product", key = "#result.id")
    @CacheEvict(value = "productsByCategory", key = "#result.category.id", beforeInvocation = false)
    public Product createProduct(ProductRequest request) {
        if (productRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Product with given name already exists");
        }
        Category category = categoryService.getCategoryByIdForProduct(request.getCategoryId());
        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        Product saved = productRepository.save(product);
        return saved;
    }

    @Override
    @Cacheable(value = "product", key = "#id")
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    @Override
    public Page<Product> getAllProducts(Pageable pageable, Long categoryId, String name, Double minPrice, Double maxPrice) {
        Specification<Product> spec = Specification.where(ProductSpecification.hasCategoryId(categoryId))
                .and(ProductSpecification.hasName(name))
                .and(ProductSpecification.hasPriceBetween(minPrice, maxPrice));

        return productRepository.findAll(spec, pageable);
    }

    private Comparator<Product> buildProductComparator(String prop) {
        if (prop == null) return null;
        return switch (prop) {
            case "price" -> Comparator.comparing(Product::getPrice, Comparator.nullsLast(Double::compareTo));
            case "name" -> Comparator.comparing(Product::getName, Comparator.nullsLast(String::compareToIgnoreCase));
            case "stockQuantity", "stock_quantity" -> Comparator.comparing(Product::getStockQuantity, Comparator.nullsLast(Integer::compareTo));
            case "category", "categoryId" -> Comparator.comparing(p -> p.getCategory() == null ? null : p.getCategory().getId(), Comparator.nullsLast(Long::compareTo));
            default -> Comparator.comparing(Product::getId, Comparator.nullsLast(Long::compareTo));
        };
    }

    @Override
    @CachePut(value = "product", key = "#id")
    @CacheEvict(value = "productsByCategory", allEntries = true)
    public Product updateProduct(Long id, ProductRequest request) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        existingProduct.setName(request.getName());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setStockQuantity(request.getStockQuantity());
        if (existingProduct.getCategory() == null || !existingProduct.getCategory().getId().equals(request.getCategoryId())) {
            Category newCat = categoryService.getCategoryByIdForProduct(request.getCategoryId());
            existingProduct.setCategory(newCat);
        }

        return productRepository.save(existingProduct);
    }

    @Override
    @CacheEvict(value = {"product", "productsByCategory"}, allEntries = true)
    public void deleteProduct(Long id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("product not found"));

        productRepository.deleteById(existingProduct.getId());
    }

    @Override
    @Cacheable(value = "productsByCategory", key = "#categoryId")
    public Page<ProductResponse> getProductsByCategoryId(Long categoryId) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Product> page = productRepository.findByCategory_Id(categoryId, pageable);
        return page.map(productMapper::toResponse);
    }

    @Override
    public Page<ProductResponse> getLowStockProducts(int threshold, Pageable pageable) {
        return productRepository.findLowStockProducts(threshold, pageable)
                .map(productMapper::toResponse);
    }

}
