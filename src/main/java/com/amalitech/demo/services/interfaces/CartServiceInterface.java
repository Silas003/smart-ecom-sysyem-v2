package com.amalitech.demo.services.interfaces;

import com.amalitech.demo.dto.CartStatus;
import com.amalitech.demo.dto.response.CartItemsReponse;
import com.amalitech.demo.dto.response.CartResponse;
import com.amalitech.demo.models.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface CartServiceInterface {
    Cart createCart(Long userId);


    CartItemsReponse addItemToCart(Long userId, Long productId, int quantity);

    @Transactional
    void removeItemFromCart(Long userId, Long cartItemId);

    @Transactional
    void clearCart(Long userId);

    Page<CartResponse> getAbandonedCarts(LocalDateTime date, Pageable pageable);
}
