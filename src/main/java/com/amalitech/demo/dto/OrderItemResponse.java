package com.amalitech.demo.dto;

public record OrderItemResponse(Long id, Long productId, Integer quantity, Double unitPrice, Double totalPrice) {
}
