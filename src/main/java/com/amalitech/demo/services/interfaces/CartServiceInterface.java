package com.amalitech.demo.services.interfaces;

import com.amalitech.demo.dto.CartStatus;
import com.amalitech.demo.dto.response.CartItemsReponse;
import com.amalitech.demo.dto.response.CartResponse;
import jakarta.transaction.Transactional;

public interface CartServiceInterface {
    CartResponse createCart(Long userId);

    CartResponse getCartByUserId(Long userId);

    @Transactional
    CartItemsReponse addItemToCart(Long userId, Long productId, int quantity);

    CartResponse updateCartStatus(Long cartId, CartStatus Status);
}
