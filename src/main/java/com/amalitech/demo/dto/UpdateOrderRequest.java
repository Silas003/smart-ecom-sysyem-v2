package com.amalitech.demo.dto;


import jakarta.validation.constraints.NotNull;

public record UpdateOrderRequest(@NotNull OrderStatus status ) {
}
