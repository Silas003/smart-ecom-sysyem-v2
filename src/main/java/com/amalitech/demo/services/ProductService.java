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
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.amalitech.demo.services.specification.ProductSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements ProductServiceInterface {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductMapper productMapper;



    @Override
    @Caching(
            put = {
                    @CachePut(value = "product", key = "#result.id"),
                    @CachePut(value = "productsByCategory", key = "#result.category.id")
            },
            evict = {
                    @CacheEvict(value = "products", allEntries = true)
            }
    )
    @Transactional
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
    @Cacheable(value = "products", keyGenerator = "productKeyGenerator")
    public Page<Product> getAllProducts(Pageable pageable, Long categoryId, String name, Double minPrice, Double maxPrice) {
        Specification<Product> spec = Specification.anyOf(ProductSpecification.hasCategoryId(categoryId))
                .and(ProductSpecification.hasName(name))
                .and(ProductSpecification.hasPriceBetween(minPrice, maxPrice));

        return productRepository.findAll(spec, pageable);
    }


    @Override
    @CachePut(value = "product", key = "#id")
    @Caching(
            put = {
                    @CachePut(value = "product", key = "#id"),
            },
            evict = {
                    @CacheEvict(value = "productsByCategory", allEntries = true),
                    @CacheEvict(value = "products", allEntries = true)
            }

    )
    @Transactional
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
    @Caching(
            evict = {
                    @CacheEvict(value = "products", allEntries = true),
                    @CacheEvict(value = {"product", "productsByCategory"}, allEntries = true)
            }
    )
    @Transactional
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
