package com.amalitech.demo.controllers;

import com.amalitech.demo.config.SecurityConfig;
import com.amalitech.demo.dto.request.CategoryRequest;
import com.amalitech.demo.dto.response.CategoryResponse;
import com.amalitech.demo.repository.CategoryRepository;
import com.amalitech.demo.restcontroller.CategoryController;
import com.amalitech.demo.security.JwtAuthenticationFilter;
import com.amalitech.demo.security.JwtService;
import com.amalitech.demo.services.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@Import(SecurityConfig.class)
public class CategoryControllerTest {

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private CategoryRepository categoryRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnAllCategories() throws Exception {
        CategoryResponse category = new CategoryResponse(1L, "Category 1");
        when(categoryService.getAllCategories()).thenReturn(List.of(category,category,category));
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnCategory() throws Exception {
        CategoryResponse category = new CategoryResponse(1L, "Category 1");
        when(categoryService.getCategoryById(anyLong())).thenReturn(category);

        mockMvc.perform(get("/api/v1/categories/5"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles= {"admin", "seller"})
    void shouldCreateCategory() throws Exception {
        CategoryRequest category = new CategoryRequest();
        category.setName("Category 1");

        when(categoryService.createCategory(category)).thenReturn(new CategoryResponse(1L, "Category 1"));

        String categoryJson = """
                {
                    "name": "Category 1"
                }
                """;

        mockMvc.perform(post("/api/v1/categories")
                        .contentType("application/json")
                        .content(categoryJson))
                .andExpect(status().isOk());

    }
}
