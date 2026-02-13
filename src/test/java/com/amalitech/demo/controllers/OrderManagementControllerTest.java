package com.amalitech.demo.controllers;

import com.amalitech.demo.config.SecurityConfig;
import com.amalitech.demo.dto.request.OrderRequest;
import com.amalitech.demo.dto.response.OrderResponse;
import com.amalitech.demo.repository.OrderItemRepository;
import com.amalitech.demo.repository.OrdersRepository;
import com.amalitech.demo.restcontroller.OrderManagementController;
import com.amalitech.demo.security.JwtAuthenticationFilter;
import com.amalitech.demo.security.JwtService;
import com.amalitech.demo.services.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderManagementController.class)
@Import(SecurityConfig.class)
public class OrderManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private OrdersRepository ordersRepository;

    @MockitoBean
    private OrderItemRepository orderItemRepository;

    @Test
    @WithMockUser(roles = {"admin","customer"})
    void shouldReturnOrdersByUser() throws Exception {
        OrderResponse r = new OrderResponse(1L, 5L, "CREATED", 100.0, List.of(), LocalDateTime.now());
        when(orderService.getOrderByUserId(5L)).thenReturn(List.of(r));

        mockMvc.perform(get("/api/v1/orders/user/5"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"admin","customer"})
    void shouldReturnOrderById() throws Exception {
        OrderResponse r = new OrderResponse(2L, 5L, "SHIPPED", 200.0, List.of(), LocalDateTime.now());
        when(orderService.getOrderById(2L)).thenReturn(r);

        mockMvc.perform(get("/api/v1/orders/2"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"admin","customer"})
    void shouldReturnPagedOrders() throws Exception {
        OrderResponse r = new OrderResponse(3L, 5L, "CREATED", 50.0, List.of(), LocalDateTime.now());
        when(orderService.getAllOrders(any())).thenReturn(new PageImpl<>(List.of(r), PageRequest.of(0,10),1));

        mockMvc.perform(get("/api/v1/orders/").param("page","0").param("size","10"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"admin","customer"})
    void shouldCreateOrder() throws Exception {
        // include at least one item to satisfy validation
        String req = "{\"userId\":1, \"items\":[{\"productId\":1,\"quantity\":1,\"unitPrice\":10.0}]}";
        OrderResponse r = new OrderResponse(4L, 1L, "CREATED", 0.0, List.of(), LocalDateTime.now());
        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(r);

        mockMvc.perform(post("/api/v1/orders/").contentType("application/json").content(req))
                .andExpect(status().isOk());
    }
}
