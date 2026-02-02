package com.amalitech.demo.services;

import com.amalitech.demo.dto.request.ProductRequest;
import com.amalitech.demo.dto.response.ProductResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.ProductMapper;
import com.amalitech.demo.models.Category;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.dao.interfaces.ProductDao;
import com.amalitech.demo.services.interfaces.ProductServiceInterface;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements ProductServiceInterface {

    private final ProductDao productDao;
    private final CategoryService categoryService;
    private final ProductMapper productMapper;

    public ProductService(ProductDao productDao, CategoryService categoryService, ProductMapper productMapper){
        this.productDao = productDao;
        this.categoryService = categoryService;
        this.productMapper = productMapper;
    }

    @Override
    public Product createProduct(ProductRequest request) {
        if( productDao.findByName(request.getName()).isPresent()){
            throw new IllegalArgumentException("Product with given name already exists");
        }
        Category category = categoryService.getCategoryById(request.getCategoryId());
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
        long total = productDao.countAll();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Product updateProduct(Long id, ProductRequest request) {
        Product existingProduct = productDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        existingProduct.setName(request.getName());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setStockQuantity(request.getStockQuantity());
        if (existingProduct.getCategory() == null || !existingProduct.getCategory().getId().equals(request.getCategoryId())){
            Category newCat = categoryService.getCategoryById(request.getCategoryId());
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
        // default page 0, size = count; we will return all if no paging requested
        int limit = Integer.MAX_VALUE; // fallback
        List<Product> products = productDao.findByCategoryId(categoryId, Integer.MAX_VALUE, 0);
        long total = productDao.countByCategoryId(categoryId);
        return new PageImpl<>(products.stream().map(productMapper::toResponse).toList(), Pageable.ofSize(products.size()), total);
    }

}
