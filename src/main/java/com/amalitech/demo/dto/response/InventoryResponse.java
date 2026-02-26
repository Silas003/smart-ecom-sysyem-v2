package com.amalitech.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Inventory response model")
public record InventoryResponse(Long id, Long productId, int stockQuantity, int reservedQuantity, String stockStatus, Long version) {
}
