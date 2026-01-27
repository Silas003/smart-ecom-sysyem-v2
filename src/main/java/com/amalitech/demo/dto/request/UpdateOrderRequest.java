package com.amalitech.demo.dto.request;


import com.amalitech.demo.dto.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderRequest(@NotNull OrderStatus status ) {
}
