package com.amalitech.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
@Schema(description = "Inventory request payload")
public class InventoryRequest {

    @NotNull
    @Schema(description = "Product id the inventory belongs to", example = "10")
    private Long productId;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Current stock quantity", example = "100")
    private Integer stockQuantity;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Reserved stock quantity", example = "5")
    private Integer reservedQuantity;

    @NotBlank
    @Schema(description = "Status description for the stock", example = "in_stock")
    private String stockStatus;
}
