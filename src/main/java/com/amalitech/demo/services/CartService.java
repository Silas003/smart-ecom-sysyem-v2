package com.amalitech.demo.services;

import com.amalitech.demo.dto.CartStatus;
import com.amalitech.demo.dto.response.CartItemsReponse;
import com.amalitech.demo.dto.response.CartResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.CartItemMapper;
import com.amalitech.demo.mapper.CartMapper;
import com.amalitech.demo.models.*;
import com.amalitech.demo.repository.*;
import com.amalitech.demo.services.interfaces.CartServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class CartService implements CartServiceInterface {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartItemsRepository cartItemsRepository;
    private final CartMapper cartMapper;
    private final ProductRepository productRepository;
    private final CartItemMapper cartItemMapper;
    private final InventoryRepository inventoryRepository;

    @CachePut(value = "activeUserCart", key = "#userId")
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Cart createCart(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.active)
                .orElseGet(() -> cartRepository.save(new Cart(user)));

        return cart;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public CartItemsReponse addItemToCart(Long userId, Long productId, int quantity) {

        Cart cart = createCart(userId);

        Product product = getProductOrThrow(productId);
        Inventory inventory = getInventoryOrThrow(productId);

        CartItems cartItem = cartItemsRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseGet(() -> new CartItems(cart, product, 0));

        int newQuantity = cartItem.getQuantity() + quantity;

        validateStockAvailability(inventory, newQuantity);


        return cartItemMapper.toResponse(cartItem);
    }

    @Override
    @CacheEvict(value = "activeUserCart", key = "#userId")
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeItemFromCart(Long userId, Long cartItemId) {

        CartItems cartItem = cartItemsRepository.findByIdWithCartAndUser(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        Cart cart = cartItem.getCart();

        if (!cart.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Cannot remove item from another user's cart");
        }

        cartItemsRepository.delete(cartItem);
    }

    @Override
    @CacheEvict(value = "activeUserCart", allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED)
    public void clearCart(Long userId) {
        // find active cart for user
        Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.active).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        // delete all items for that cart
        List<CartItems> items = cartItemsRepository.findByCartId(cart.getId());
        if (!items.isEmpty()) {
            cartItemsRepository.deleteAll(items);
        }

        cartRepository.save(cart);
    }

    @Override
    public Page<CartResponse> getAbandonedCarts(LocalDateTime date, Pageable pageable) {
        return cartRepository.findAbandonedCarts(date, pageable).map(this::buildCartResponse);
    }

    private CartResponse buildCartResponse(Cart cart) {
        List<CartItemsReponse> items = cartItemsRepository.findByCartId(cart.getId()).stream().map(cartItemMapper::toResponse).toList();
        return cartMapper.toResponse(cart, items);
    }



    private Product getProductOrThrow(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));
    }

    private Inventory getInventoryOrThrow(Long productId) {
        return inventoryRepository.findByProductId(productId).orElseThrow(() -> new EntityNotFoundException("Inventory not found for product ID: " + productId));
    }



    private void validateStockAvailability(Inventory inventory, int requestedQuantity) {

        if (inventory.getStockQuantity() < requestedQuantity) {
            throw new IllegalArgumentException(String.format("Insufficient stock for product Available: %d, Requested: %d", inventory.getStockQuantity(), requestedQuantity));
        }
    }


}
