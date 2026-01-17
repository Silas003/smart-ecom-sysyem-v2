package com.amalitech.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class InventoryRequest {

    @NotNull
    private Long productId;

    @NotNull
    @PositiveOrZero
    private Integer stockQuantity;

    @NotNull
    @PositiveOrZero
    private Integer reservedQuantity;

    @NotBlank
    private String stockStatus;
}
