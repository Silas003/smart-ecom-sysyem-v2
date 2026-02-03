package com.amalitech.demo.dto.request;

public record CartItemRequest(int cartId,
    int productId,
    int quantity,
    Double unitPrice, Double totalPrice){
}
