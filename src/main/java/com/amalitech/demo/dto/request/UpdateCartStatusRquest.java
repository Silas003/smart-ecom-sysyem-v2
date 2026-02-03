package com.amalitech.demo.dto.request;

import com.amalitech.demo.dto.CartStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateCartStatusRquest(@NotNull CartStatus status) {
}
