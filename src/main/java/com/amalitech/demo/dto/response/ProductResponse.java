package com.amalitech.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Product response model")
public record ProductResponse(
        Long id,
        String name,
        Double price,
        Integer stockQuantity,
        Long categoryId
) {
}
