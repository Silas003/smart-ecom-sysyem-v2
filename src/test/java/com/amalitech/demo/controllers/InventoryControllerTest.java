package com.amalitech.demo.controllers;

import com.amalitech.demo.dto.request.InventoryRequest;
import com.amalitech.demo.dto.response.InventoryResponse;
import com.amalitech.demo.restcontroller.InventoryController;
import com.amalitech.demo.services.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
public class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventoryService inventoryService;

    @Test
    void shouldReturnInventories() throws Exception {
        InventoryResponse r = new InventoryResponse(1L, 1L, 10, 0, "IN_STOCK", null);
        when(inventoryService.getAllInventories()).thenReturn(List.of(r));

        mockMvc.perform(get("/api/v1/inventories/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("inventories retrieved"));
    }

    @Test
    void shouldCreateInventory() throws Exception {
        // include required fields: productId, stockQuantity, reservedQuantity, stockStatus
        String req = "{\"productId\":1, \"stockQuantity\":5, \"reservedQuantity\":0, \"stockStatus\":\"IN_STOCK\"}";
        InventoryResponse r = new InventoryResponse(2L, 1L, 5, 0, "IN_STOCK", null);
        when(inventoryService.createInventory(any(InventoryRequest.class))).thenReturn(r);

        mockMvc.perform(post("/api/v1/inventories/create_inventory").contentType("application/json").content(req))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("inventory created"));
    }
}
