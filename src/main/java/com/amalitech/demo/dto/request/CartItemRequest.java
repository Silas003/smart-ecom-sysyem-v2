package com.amalitech.demo.dto.request;

public record CartItemRequest(Long cartId,
    Long productId,
    int quantity,
    Double unitPrice, Double totalPrice){
}
