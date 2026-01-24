package com.amalitech.demo.dto;

import java.time.LocalDateTime;

public record OrderResponse(Long id, Long userId,String status ,Double totalAmount, LocalDateTime orderDate) {
}
