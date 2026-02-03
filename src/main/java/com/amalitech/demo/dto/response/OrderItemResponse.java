package com.amalitech.demo.dto.response;

public record OrderItemResponse(Long id, Long productId, Integer quantity, Double unitPrice, Double totalPrice) {
}
