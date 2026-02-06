package com.amalitech.demo.dto.response;

import java.util.List;

public record CartResponse(Long id, long userId, String status, List<CartItemsReponse> items) {
}
