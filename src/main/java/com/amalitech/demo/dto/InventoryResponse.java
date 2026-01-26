package com.amalitech.demo.dto;

public record InventoryResponse(Long id, Long productId, int stockQuantity, int reservedQuantity, String stockStatus, Long version) {
}
