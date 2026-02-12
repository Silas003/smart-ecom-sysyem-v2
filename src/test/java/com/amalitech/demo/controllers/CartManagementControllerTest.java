package com.amalitech.demo.controllers;

import com.amalitech.demo.dto.response.CartItemsReponse;
import com.amalitech.demo.dto.response.CartResponse;
import com.amalitech.demo.restcontroller.CartManagementController;
import com.amalitech.demo.services.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartManagementController.class)
public class CartManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @Test
    void shouldCreateCart() throws Exception {
        CartResponse resp = new CartResponse(1L, 1L, "OPEN", List.of());
        when(cartService.createCart(5L)).thenReturn(resp);

        mockMvc.perform(get("/api/v1/carts/users/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cart retrieved successfully"));
    }

    @Test
    void shouldAddItemToCart() throws Exception {
        CartItemsReponse r = new CartItemsReponse(7L, 1L, 2L, 2.0, 6.0, 3);
        when(cartService.addItemToCart(1L,2L,3)).thenReturn(r);

        mockMvc.perform(post("/api/v1/carts/users/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":2,\"quantity\":3}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Item added to cart successfully"));
    }

    @Test
    void shouldGetCart() throws Exception {
        CartResponse resp = new CartResponse(1L, 5L, "OPEN", List.of());
        when(cartService.createCart(5L)).thenReturn(resp);

        mockMvc.perform(get("/api/v1/carts/users/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cart retrieved successfully"));
    }
}
