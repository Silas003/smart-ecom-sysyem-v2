package com.amalitech.demo.dto.response;

public record InventoryResponse(Long id, Long productId, int stockQuantity, int reservedQuantity, String stockStatus, Long version) {
}
