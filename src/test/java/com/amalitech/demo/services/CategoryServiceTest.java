package com.amalitech.demo.services;


import com.amalitech.demo.dao.interfaces.CategoryDao;
import com.amalitech.demo.dto.request.CategoryRequest;
import com.amalitech.demo.dto.response.CategoryResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.CategoryMapper;
import com.amalitech.demo.models.Category;
import com.amalitech.demo.utils.Sorter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryDao categoryDao;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private Sorter<Category> sorter;

    @Test
    void shouldReturnAllCategories() {
        Category category = new Category(1L, "Electronics");
        CategoryResponse response = new CategoryResponse(1L, "Electronics");

        List<Category> categories = new ArrayList<>();
        categories.add(category);

        when(categoryDao.findAll()).thenReturn(categories);
        when(sorter.sort(eq(categories), any())).thenReturn(categories);
        when(categoryMapper.toDto(anyList())).thenReturn(List.of(response));

        List<CategoryResponse> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("Electronics", result.get(0).name());

        verify(categoryDao, times(1)).findAll();
        verify(sorter, times(1)).sort(eq(categories), any());
        verify(categoryMapper, times(1)).toDto(anyList());
    }

    @Test
    void getAllCategories_empty_returnsEmptyList() {
        when(categoryDao.findAll()).thenReturn(List.of());
        List<CategoryResponse> result = categoryService.getAllCategories();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(categoryDao, times(1)).findAll();
        verifyNoInteractions(sorter);
    }

    @Test
    void getCategoryById_notFound_throws() {
        when(categoryDao.findById(5L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> categoryService.getCategoryById(5L));
        verify(categoryDao, times(1)).findById(5L);
    }

    @Test
    void createCategory_duplicateName_throws() {
        CategoryRequest req = new CategoryRequest();
        req.setName("Books");
        when(categoryDao.findByName("Books")).thenReturn(Optional.of(new Category(2L, "Books")));
        assertThrows(IllegalArgumentException.class, () -> categoryService.createCategory(req));
        verify(categoryDao, times(1)).findByName("Books");
        verify(categoryDao, never()).save(any());
    }

    @Test
    void updateCategory_success() {
        Category existing = new Category(3L, "Toys");
        CategoryRequest req = new CategoryRequest();
        req.setName("Kids Toys");
        when(categoryDao.findById(3L)).thenReturn(Optional.of(existing));
        when(categoryMapper.toDto(existing)).thenReturn(new CategoryResponse(3L, "Kids Toys"));

        CategoryResponse resp = categoryService.updateCategory(3L, req);
        assertNotNull(resp);
        verify(categoryDao, times(1)).findById(3L);
        verify(categoryDao, times(1)).update(existing);
    }

    @Test
    void updateCategory_notFound_throws() {
        CategoryRequest req = new CategoryRequest();
        req.setName("NewName");
        when(categoryDao.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> categoryService.updateCategory(99L, req));
        verify(categoryDao, times(1)).findById(99L);
    }

    @Test
    void deleteCategory_invokesDao() {
        categoryService.deleteCategory(10L);
        verify(categoryDao, times(1)).deleteById(10L);
    }
}
