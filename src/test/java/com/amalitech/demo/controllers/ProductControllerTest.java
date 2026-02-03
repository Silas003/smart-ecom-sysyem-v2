package com.amalitech.demo.controllers;

import com.amalitech.demo.dto.request.ProductRequest;
import com.amalitech.demo.dto.response.ProductResponse;
import com.amalitech.demo.mapper.ProductMapper;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.restcontroller.ProductController;
import com.amalitech.demo.services.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;


import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ProductMapper productMapper;

    @Test
    void shouldReturnPagedProducts() throws Exception {
        Product p = new Product(); p.setId(1L); p.setName("P1"); p.setPrice(9.99);
        ProductResponse pr = new ProductResponse(1L, "P1", 9.99, 5, 1L);
        when(productService.getAllProducts(any())).thenReturn(new PageImpl<>(List.of(p), PageRequest.of(0,10),1));
        when(productMapper.toResponse(any(Product.class))).thenReturn(pr);

        mockMvc.perform(get("/api/v1/products/").param("page","0").param("size","10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("products retrieved"))
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }

    @Test
    void shouldReturnProductById() throws Exception {
        Product p = new Product(); p.setId(2L); p.setName("P2"); p.setPrice(0.0);
        ProductResponse pr = new ProductResponse(2L, "P2", 0.0, 0, 1L);
        when(productService.getProductById(2L)).thenReturn(p);
        when(productMapper.toResponse(p)).thenReturn(pr);

        mockMvc.perform(get("/api/v1/products/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("product retrieved"))
                .andExpect(jsonPath("$.data.id").value(2));
    }

    @Test
    void shouldCreateProduct() throws Exception {
        // include required fields: name, price, stockQuantity, categoryId
        String req = "{\"name\":\"New\",\"description\":\"d\",\"price\":10.0,\"stockQuantity\":5,\"categoryId\":1}";
        Product created = new Product(); created.setId(5L); created.setName("New"); created.setPrice(10.0);
        ProductResponse pr = new ProductResponse(5L, "New", 10.0, 0, 1L);
        when(productService.createProduct(any(ProductRequest.class))).thenReturn(created);
        when(productMapper.toResponse(created)).thenReturn(pr);

        mockMvc.perform(post("/api/v1/products/create_product").contentType("application/json").content(req))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("product created "));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/v1/products/7"))
                .andExpect(status().isNoContent());
    }
}
