package com.amalitech.demo.dto;

public record CartItemRequest(
        int cartId,
    int productId,
    int quantity,
    Double unitPrice, Double totalPrice){
}
