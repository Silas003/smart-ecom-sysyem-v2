package com.amalitech.demo.controllers;

import com.amalitech.demo.dto.request.CategoryRequest;
import com.amalitech.demo.dto.response.CategoryResponse;
import com.amalitech.demo.models.Category;
import com.amalitech.demo.restcontroller.CategoryController;
import com.amalitech.demo.services.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnAllCategories() throws Exception {
        CategoryResponse category = new CategoryResponse(1L,"category");
        when(categoryService.getAllCategories()).thenReturn(List.of(category,category,category));
        mockMvc.perform(get("/api/v1/categories/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("categories retrieved"))
                .andExpect(jsonPath("$.data.length()").value(3));
    }

    @Test
    void shouldReturnCategory() throws Exception {
        Category category = new Category();
        category.setName("Category 1");
        when(categoryService.getCategoryById(anyLong())).thenReturn(new CategoryResponse(1L,"Category 1"));

        mockMvc.perform(get("/api/v1/categories/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("category retrieved"));
    }

    @Test
    void shouldCreateCategory() throws Exception {
        CategoryRequest category = new CategoryRequest();
        category.setName("Category 1");

        when(categoryService.createCategory(category)).thenReturn(new CategoryResponse(1L,"category"));

        String categoryJson = """
                {
                    "name": "Category 1"
                }
                """;

        mockMvc.perform(post("/api/v1/categories/create_category")
                        .contentType("application/json")
                        .content(categoryJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("category created"));
    }
}
