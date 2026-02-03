package com.amalitech.demo.services;

import com.amalitech.demo.dto.request.ProductRequest;
import com.amalitech.demo.dto.response.ProductResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.ProductMapper;
import com.amalitech.demo.models.Category;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.dao.interfaces.ProductDao;
import com.amalitech.demo.services.interfaces.ProductServiceInterface;
import com.amalitech.demo.utils.Sorter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ProductService implements ProductServiceInterface {

    private final ProductDao productDao;
    private final CategoryService categoryService;
    private final ProductMapper productMapper;
    // use injected sorter bean (generic)
    private final Sorter<Product> sorter;

    public ProductService(ProductDao productDao, CategoryService categoryService, ProductMapper productMapper, Sorter<Product> sorter){
        this.productDao = productDao;
        this.categoryService = categoryService;
        this.productMapper = productMapper;
        this.sorter = sorter;
    }

    @Override
    public Product createProduct(ProductRequest request) {
        if( productDao.findByName(request.getName()).isPresent()){
            throw new IllegalArgumentException("Product with given name already exists");
        }
        Category category = categoryService.getCategoryByIdForProduct(request.getCategoryId());
        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        productDao.save(product);
        return product;
    }

    @Override
    public Product getProductById(Long id) {
        return productDao.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        int offset = pageNumber * pageSize;
        List<Product> content = productDao.findAll(pageSize, offset);
        if (content == null) content = List.of();

        // Apply merge-sort if pageable has sorting criteria
        Sort sort = pageable.getSort();
        if (sort.isSorted() && !content.isEmpty()) {
            // pick the first sort order (supporting single-field sorting)
            Sort.Order order = sort.iterator().next();
            Comparator<Product> cmp = buildProductComparator(order.getProperty());
            if (cmp != null) {
                if (order.isDescending()) cmp = cmp.reversed();
                content = sorter.sort(content, cmp);
            }
        }

        long total = productDao.countAll();
        return new PageImpl<>(content, pageable, total);
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
    public Product updateProduct(Long id, ProductRequest request) {
        Product existingProduct = productDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        existingProduct.setName(request.getName());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setStockQuantity(request.getStockQuantity());
        if (existingProduct.getCategory() == null || !existingProduct.getCategory().getId().equals(request.getCategoryId())){
            Category newCat = categoryService.getCategoryByIdForProduct(request.getCategoryId());
            existingProduct.setCategory(newCat);
        }

        productDao.update(existingProduct);
        return existingProduct;
    }


    @Override
    public void deleteProduct(Long id) {
        Product existingProduct = productDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("product not found"));

        productDao.deleteById(existingProduct.getId());
    }

    @Override
    public Page<ProductResponse> getProductsByCategoryId(Long categoryId){
        List<Product> products = productDao.findByCategoryId(categoryId, Integer.MAX_VALUE, 0);
        if (products == null) products = List.of();
        // apply stable, service-level sorting by product name before mapping
        if (!products.isEmpty()) {
            products = sorter.sort(products, Comparator.comparing(Product::getName, Comparator.nullsLast(String::compareToIgnoreCase)));
        }
        long total = productDao.countByCategoryId(categoryId);
        return new PageImpl<>(products.stream().map(productMapper::toResponse).toList(), Pageable.ofSize(products.size()), total);
    }

}
