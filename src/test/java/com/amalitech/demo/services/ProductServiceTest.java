package com.amalitech.demo.services;

import com.amalitech.demo.dto.request.ProductRequest;
import com.amalitech.demo.dto.response.ProductResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.ProductMapper;
import com.amalitech.demo.models.Category;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.repository.ProductRepository;
import com.amalitech.demo.utils.Sorter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private Sorter<Product> sorter;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldCreateProductSuccessfully() {
        ProductRequest req = new ProductRequest();
        req.setName("P1");
        req.setPrice(12.5);
        req.setStockQuantity(5);
        req.setCategoryId(2L);

        Category cat = new Category(2L, "Cat");
        when(productRepository.existsByName("P1")).thenReturn(false);
        when(categoryService.getCategoryByIdForProduct(2L)).thenReturn(cat);
        Product p = new Product();
        when(productMapper.toEntity(req)).thenReturn(p);
        when(productRepository.save(p)).thenReturn(p);

        Product created = productService.createProduct(req);
        assertNotNull(created);
        verify(productRepository, times(1)).existsByName("P1");
        verify(categoryService, times(1)).getCategoryByIdForProduct(2L);
        verify(productRepository, times(1)).save(p);
    }

    @Test
    void createProduct_duplicateName_throws() {
        ProductRequest req = new ProductRequest();
        req.setName("P1");
        when(productRepository.existsByName("P1")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> productService.createProduct(req));
        verify(productRepository, times(1)).existsByName("P1");
    }

    @Test
    void shouldGetProductById() {
        Product p = new Product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));
        Product out = productService.getProductById(1L);
        assertSame(p, out);
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_notFound_throws() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    void shouldGetAllProducts_withSorting() {
        int page = 0, size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by("price").descending());
        Product p1 = new Product(); p1.setId(1L); p1.setPrice(5.0);
        Product p2 = new Product(); p2.setId(2L); p2.setPrice(10.0);
        Page<Product> pageData = new PageImpl<>(List.of(p1, p2), pageable, 2);
        when(productRepository.findAll(pageable)).thenReturn(pageData);
        when(sorter.sort(anyList(), any())).thenReturn(List.of(p2, p1));

        Page<Product> result = productService.getAllProducts(pageable, null);
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(sorter, times(1)).sort(anyList(), any());
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldUpdateProduct() {
        Product existing = new Product(); existing.setId(5L);
        existing.setCategory(new Category(1L, "C1"));
        when(productRepository.findById(5L)).thenReturn(Optional.of(existing));

        ProductRequest req = new ProductRequest();
        req.setName("NewName");
        req.setPrice(99.0);
        req.setStockQuantity(10);
        req.setCategoryId(2L);

        Category newCat = new Category(2L, "C2");
        when(categoryService.getCategoryByIdForProduct(2L)).thenReturn(newCat);
        when(productRepository.save(existing)).thenReturn(existing);

        Product updated = productService.updateProduct(5L, req);
        assertEquals("NewName", updated.getName());
        assertEquals(99.0, updated.getPrice());
        verify(productRepository, times(1)).findById(5L);
        verify(productRepository, times(1)).save(existing);
    }

    @Test
    void shouldDeleteProduct() {
        Product existing = new Product(); existing.setId(7L);
        when(productRepository.findById(7L)).thenReturn(Optional.of(existing));
        productService.deleteProduct(7L);
        verify(productRepository, times(1)).deleteById(7L);
    }

    @Test
    void shouldGetProductsByCategoryId_sortedAndMapped() {
        Product a = new Product(); a.setId(1L); a.setName("A");
        Product b = new Product(); b.setId(2L); b.setName("B");
        Page<Product> page = new PageImpl<>(List.of(b, a), Pageable.unpaged(), 2);
        when(productRepository.findByCategory_Id(3L, Pageable.unpaged())).thenReturn(page);
        when(sorter.sort(anyList(), any())).thenReturn(List.of(a, b));
        ProductResponse ra = new ProductResponse(1L, "A", 5.0, 2, 10L);
        ProductResponse rb = new ProductResponse(2L, "B", 7.0, 3, 10L);
        when(productMapper.toResponse(a)).thenReturn(ra);
        when(productMapper.toResponse(b)).thenReturn(rb);

        var resultPage = productService.getProductsByCategoryId(3L);
        assertNotNull(resultPage);
        assertEquals(2, resultPage.getContent().size());
        assertEquals("A", resultPage.getContent().get(0).name());
        verify(sorter, times(1)).sort(anyList(), any());
        verify(productMapper, times(2)).toResponse(any());
    }
}
