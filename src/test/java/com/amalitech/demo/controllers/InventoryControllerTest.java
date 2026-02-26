package com.amalitech.demo.controllers;

import com.amalitech.demo.config.SecurityConfig;
import com.amalitech.demo.dto.request.InventoryRequest;
import com.amalitech.demo.dto.response.InventoryResponse;
import com.amalitech.demo.repository.InventoryRepository;
import com.amalitech.demo.repository.ProductRepository;
import com.amalitech.demo.restcontroller.InventoryController;
import com.amalitech.demo.security.JwtAuthenticationFilter;
import com.amalitech.demo.security.JwtService;
import com.amalitech.demo.services.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
@Import(SecurityConfig.class)
public class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventoryService inventoryService;

    @MockitoBean
    private InventoryRepository inventoryRepository;

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldReturnInventories() throws Exception {
        InventoryResponse r = new InventoryResponse(1L, 1L, 10, 0, "IN_STOCK", null);
        when(inventoryService.getAllInventories()).thenReturn(List.of(r));

        mockMvc.perform(get("/api/v1/inventories/"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreateInventory() throws Exception {
        // include required fields: productId, stockQuantity, reservedQuantity, stockStatus
        String req = "{\"productId\":1, \"stockQuantity\":5, \"reservedQuantity\":0, \"stockStatus\":\"IN_STOCK\"}";
        InventoryResponse r = new InventoryResponse(2L, 1L, 5, 0, "IN_STOCK", null);
        when(inventoryService.createInventory(any(InventoryRequest.class))).thenReturn(r);

        mockMvc.perform(post("/api/v1/inventories").contentType("application/json").content(req))
                .andExpect(status().isOk());
    }
}
