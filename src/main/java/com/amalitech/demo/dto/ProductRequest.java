package com.amalitech.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductRequest {
    @NotBlank(message = "Product name cannot be blank")
    private String name;

    @NotNull
    @Min(value = 0, message = "Price must be 0 or greater")
    private Double price;

    @NotNull
    @Min(value = 0, message = "Stock quantity must be 0 or greater")
    private Integer stockQuantity;

    @NotNull(message = "Category id is required")
    private Long categoryId;
}
