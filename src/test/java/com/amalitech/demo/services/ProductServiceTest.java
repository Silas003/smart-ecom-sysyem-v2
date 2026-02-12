package com.amalitech.demo.services;

import com.amalitech.demo.dao.interfaces.ProductDao;
import com.amalitech.demo.dto.request.ProductRequest;
import com.amalitech.demo.dto.response.ProductResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.ProductMapper;
import com.amalitech.demo.models.Category;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.utils.Sorter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
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
    private ProductDao productDao;

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
        when(productDao.findByName("P1")).thenReturn(Optional.empty());
        when(categoryService.getCategoryByIdForProduct(2L)).thenReturn(cat);
        Product p = new Product();
        when(productMapper.toEntity(req)).thenReturn(p);
        when(productDao.save(p)).thenReturn(42L);

        Product created = productService.createProduct(req);
        assertNotNull(created);
        verify(productDao, times(1)).findByName("P1");
        verify(categoryService, times(1)).getCategoryByIdForProduct(2L);
        verify(productDao, times(1)).save(p);
    }

    @Test
    void createProduct_duplicateName_throws() {
        ProductRequest req = new ProductRequest();
        req.setName("P1");
        when(productDao.findByName("P1")).thenReturn(Optional.of(new Product()));
        assertThrows(IllegalArgumentException.class, () -> productService.createProduct(req));
        verify(productDao, times(1)).findByName("P1");
    }

    @Test
    void shouldGetProductById() {
        Product p = new Product();
        when(productDao.findById(1L)).thenReturn(Optional.of(p));
        Product out = productService.getProductById(1L);
        assertSame(p, out);
        verify(productDao, times(1)).findById(1L);
    }

    @Test
    void getProductById_notFound_throws() {
        when(productDao.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    void shouldGetAllProducts_withSorting() {
        int page = 0, size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by("price").descending());
        Product p1 = new Product(); p1.setId(1L); p1.setPrice(5.0);
        Product p2 = new Product(); p2.setId(2L); p2.setPrice(10.0);
        when(productDao.findAll(size, page * size)).thenReturn(List.of(p1, p2));
        when(productDao.countAll()).thenReturn(2L);
        // sorter expected to be called because pageable has sort
        when(sorter.sort(anyList(), any())).thenReturn(List.of(p2, p1));

        Page<Product> result = productService.getAllProducts(pageable,any());
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        // ensure sorter was used
        verify(sorter, times(1)).sort(anyList(), any());
        verify(productDao, times(1)).findAll(size, page * size);
    }

    @Test
    void shouldUpdateProduct() {
        Product existing = new Product(); existing.setId(5L);
        existing.setCategory(new Category(1L, "C1"));
        when(productDao.findById(5L)).thenReturn(Optional.of(existing));

        ProductRequest req = new ProductRequest();
        req.setName("NewName");
        req.setPrice(99.0);
        req.setStockQuantity(10);
        req.setCategoryId(2L);

        Category newCat = new Category(2L, "C2");
        when(categoryService.getCategoryByIdForProduct(2L)).thenReturn(newCat);

        Product updated = productService.updateProduct(5L, req);
        assertEquals("NewName", updated.getName());
        assertEquals(99.0, updated.getPrice());
        verify(productDao, times(1)).update(existing);
    }

    @Test
    void shouldDeleteProduct() {
        Product existing = new Product(); existing.setId(7L);
        when(productDao.findById(7L)).thenReturn(Optional.of(existing));
        productService.deleteProduct(7L);
        verify(productDao, times(1)).deleteById(7L);
    }

    @Test
    void shouldGetProductsByCategoryId_sortedAndMapped() {
        Product a = new Product(); a.setId(1L); a.setName("A");
        Product b = new Product(); b.setId(2L); b.setName("B");
        when(productDao.findByCategoryId(3L, Integer.MAX_VALUE, 0)).thenReturn(List.of(b, a));
        when(sorter.sort(anyList(), any())).thenReturn(List.of(a, b));
        ProductResponse ra = new ProductResponse(1L, "A", 5.0, 2, 10L);
        ProductResponse rb = new ProductResponse(2L, "B", 7.0, 3, 10L);
        when(productMapper.toResponse(a)).thenReturn(ra);
        when(productMapper.toResponse(b)).thenReturn(rb);

        var page = productService.getProductsByCategoryId(3L);
        assertNotNull(page);
        assertEquals(2, page.getContent().size());
        assertEquals("A", page.getContent().get(0).name());
        verify(sorter, times(1)).sort(anyList(), any());
        verify(productMapper, times(2)).toResponse(any());
    }
}
