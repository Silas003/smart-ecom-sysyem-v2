package com.amalitech.demo.controllers;

import com.amalitech.demo.dto.request.OrderRequest;
import com.amalitech.demo.dto.request.UpdateOrderRequest;
import com.amalitech.demo.dto.response.OrderItemResponse;
import com.amalitech.demo.dto.response.OrderResponse;
import com.amalitech.demo.restcontroller.OrderManagementController;
import com.amalitech.demo.services.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
public class OrderManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void shouldReturnOrdersByUser() throws Exception {
        OrderResponse r = new OrderResponse(1L, 5L, "CREATED", 100.0, List.of(), LocalDateTime.now());
        when(orderService.getOrderByUserId(5L)).thenReturn(List.of(r));

        mockMvc.perform(get("/api/v1/orders/user/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("user orders retrieved"))
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void shouldReturnOrderById() throws Exception {
        OrderResponse r = new OrderResponse(2L, 5L, "SHIPPED", 200.0, List.of(), LocalDateTime.now());
        when(orderService.getOrderById(2L)).thenReturn(r);

        mockMvc.perform(get("/api/v1/orders/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("order retrieved"))
                .andExpect(jsonPath("$.data.id").value(2));
    }

    @Test
    void shouldReturnPagedOrders() throws Exception {
        OrderResponse r = new OrderResponse(3L, 5L, "CREATED", 50.0, List.of(), LocalDateTime.now());
        when(orderService.getAllOrders(any())).thenReturn(new PageImpl<>(List.of(r), PageRequest.of(0,10),1));

        mockMvc.perform(get("/api/v1/orders/").param("page","0").param("size","10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("orders retrieved"))
                .andExpect(jsonPath("$.data.content[0].id").value(3));
    }

    @Test
    void shouldCreateOrder() throws Exception {
        // include at least one item to satisfy validation
        String req = "{\"userId\":1, \"items\":[{\"productId\":1,\"quantity\":1,\"unitPrice\":10.0}]}";
        OrderResponse r = new OrderResponse(4L, 1L, "CREATED", 0.0, List.of(), LocalDateTime.now());
        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(r);

        mockMvc.perform(post("/api/v1/orders/").contentType("application/json").content(req))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("order created"));
    }
}
