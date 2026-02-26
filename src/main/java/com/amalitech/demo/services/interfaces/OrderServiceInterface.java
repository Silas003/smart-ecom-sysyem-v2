package com.amalitech.demo.services.interfaces;

import com.amalitech.demo.dto.OrderStatus;
import com.amalitech.demo.dto.request.OrderRequest;
import com.amalitech.demo.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderServiceInterface {

    List<OrderResponse> getOrderByUserId(Long userId);

    OrderResponse getOrderById(Long id);

    Page<OrderResponse> getAllOrders(Pageable pageable, Long userId, OrderStatus status, LocalDateTime start, LocalDateTime end);

    void deleteOrder(Long orderId);


    OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus);


    OrderResponse createOrder(OrderRequest req);


    // Native-query-backed reporting: get a user's orders within a date range using pagination
    Page<OrderResponse> getUserOrdersWithinPeriod(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Double getTotalRevenue(LocalDateTime start, LocalDateTime end);
}
