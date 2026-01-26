package com.amalitech.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

//public record OrderResponse(Long id, Long userId,String status ,Double totalAmount, LocalDateTime orderDate) {}
public record OrderResponse(Long id, Long userId, String status, Double totalAmount, List<OrderItemResponse> items, LocalDateTime createdAt) {
}
