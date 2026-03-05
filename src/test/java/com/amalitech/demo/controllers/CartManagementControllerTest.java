package com.amalitech.demo.controllers;

import com.amalitech.demo.config.SecurityConfig;
import com.amalitech.demo.dto.response.CartItemsReponse;
import com.amalitech.demo.models.Cart;
import com.amalitech.demo.restcontroller.CartManagementController;
import com.amalitech.demo.security.JwtAuthenticationFilter;
import com.amalitech.demo.security.JwtService;
import com.amalitech.demo.services.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CartManagementController.class)
@Import(SecurityConfig.class)
public class CartManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CartService cartService;

    @Test
    @WithMockUser(roles= {"admin", "customer"})
    void shouldCreateCart() throws Exception {
        Cart resp = new Cart();
        when(cartService.createCart(5L)).thenReturn(resp);

        mockMvc.perform(get("/api/v1/carts/users/5"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles= {"admin", "customer"})
    void shouldAddItemToCart() throws Exception {
        CartItemsReponse r = new CartItemsReponse(7L, 1L, 2L, 2.0, 6.0, 3);
        when(cartService.addItemToCart(1L,2L,3)).thenReturn(r);

        mockMvc.perform(post("/api/v1/carts/users/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":2,\"quantity\":3}"))
                .andDo(print())

                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles= {"admin", "customer"})
    void shouldGetCart() throws Exception {
        Cart resp = new Cart();
        when(cartService.createCart(5L)).thenReturn(resp);

        mockMvc.perform(get("/api/v1/carts/users/5"))
                .andExpect(status().isOk());
    }
}
