package com.amalitech.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Product creation/update payload")
public class ProductRequest {
    @NotBlank(message = "Product name cannot be blank")
    @Schema(description = "Name of the product", example = "Bluetooth Headphones")
    private String name;

    @NotNull
    @Min(value = 0, message = "Price must be 0 or greater")
    @Schema(description = "Price in USD", example = "79.99")
    private Double price;

    @NotNull
    @Min(value = 0, message = "Stock quantity must be 0 or greater")
    @Schema(description = "Available stock quantity", example = "100")
    private Integer stockQuantity;

    @NotNull(message = "Category id is required")
    @Schema(description = "Category id for the product", example = "2")
    private Long categoryId;
}
