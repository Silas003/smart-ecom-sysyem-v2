package com.amalitech.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ProductInput {
    @NotNull
    private String name;

    @NotNull
    private Double price;

    @NotNull
    @PositiveOrZero
    private Integer stockQuantity;

    @NotNull
    private Long categoryId;
}
