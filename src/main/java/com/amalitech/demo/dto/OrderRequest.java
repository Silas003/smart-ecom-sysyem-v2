package com.amalitech.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    @NotNull
    @PositiveOrZero
    private Long userId;

    @NotEmpty(message = "order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;
}
