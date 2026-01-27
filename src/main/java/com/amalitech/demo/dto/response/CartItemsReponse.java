package com.amalitech.demo.dto.response;

public record CartItemsReponse(Long id,Long cartId, Long productId, Double unitPrice, Double totalPrice, Integer quantity) {
}
