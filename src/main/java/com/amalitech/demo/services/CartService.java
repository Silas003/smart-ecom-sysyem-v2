package com.amalitech.demo.services;

import com.amalitech.demo.dto.CartStatus;
import com.amalitech.demo.dto.response.CartItemsReponse;
import com.amalitech.demo.dto.response.CartResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.CartItemMapper;
import com.amalitech.demo.mapper.CartMapper;
import com.amalitech.demo.models.*;
import com.amalitech.demo.repository.CartItemsRepository;
import com.amalitech.demo.repository.CartRepository;
import com.amalitech.demo.repository.InventoryRepository;
import com.amalitech.demo.repository.ProductRepository;
import com.amalitech.demo.repository.UserRepository;
import com.amalitech.demo.services.interfaces.CartServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

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

    @CachePut(value = "activeUserCart", key = "#result.id + #userId")
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public CartResponse createCart(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User not found"));
        boolean exists = cartRepository.existsByUserIdAndStatus(user.getId(), CartStatus.active);
        if (exists) {
            Cart cart = cartRepository.findByUserIdAndStatus(user.getId(), CartStatus.active).orElseThrow(
                    () -> new EntityNotFoundException("cart not found")
            );
            return buildCartResponse(cart);
        }
        Cart cart = cartRepository.save(new Cart(user, "active"));

        return buildCartResponse(cart);
    }

    @Cacheable(value = "cart", key = "#userId")
    @Override
    public CartResponse getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.active)
                .orElseThrow(() -> new EntityNotFoundException("cart not found"));
        return buildCartResponse(cart);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public CartItemsReponse addItemToCart(Long userId, Long productId, int quantity) {
        // 1. Validate and get cart
        Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.active)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        // 2. Validate product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        // 3. VALIDATE INVENTORY AVAILABILITY - CRITICAL!
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found for product"));

        // 4. Check if product already in cart
        CartItems cartItems = cartItemsRepository.findByProductIdAndCartId(productId, cart.getId())
                .orElse(null);

        int newQuantity = quantity;
        if (cartItems != null) {
            newQuantity = cartItems.getQuantity() + quantity;
        }

        // 5. Validate sufficient stock for new total quantity
        if (inventory.getStockQuantity() < newQuantity) {
            throw new IllegalArgumentException(
                    "Insufficient stock for product: " + product.getName()
                            + ". Available: " + inventory.getStockQuantity()
                            + ", Requested: " + newQuantity
            );
        }

        // 6. Update existing item or create new one
        if (cartItems != null) {
            cartItems.setQuantity(newQuantity);
            cartItems.setTotalPrice(newQuantity * product.getPrice());
            cartItemsRepository.save(cartItems);
        } else {
            cartItems = new CartItems();
            cartItems.setCart(cart);
            cartItems.setProduct(product);
            cartItems.setQuantity(newQuantity);
            cartItems.setUnitPrice(product.getPrice());
            cartItems.setTotalPrice(newQuantity * product.getPrice());
            cartItems = cartItemsRepository.save(cartItems);
        }

        return cartItemMapper.toResponse(cartItems);
    }

    @CachePut(value = "activeUserCart", key = "#cartId + result.userId")
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public CartResponse updateCartStatus(Long cartId, CartStatus Status) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new EntityNotFoundException("cart not found"));

        cart.setStatus(Status);
        Cart saved = cartRepository.save(cart);
        return buildCartResponse(saved);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeItemFromCart(Long userId, Long cartItemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        CartItems cartItem = cartItemsRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        Long cartId = cartItem.getCart().getId();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        if (!cart.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Cannot remove item from another user's cart");
        }

        // 3. Verify cart is active (cannot remove items from completed/abandoned carts)
        if (cart.getStatus() != CartStatus.active) {
            throw new IllegalStateException("Cannot remove items from a " + cart.getStatus() + " cart");
        }

        // 4. Delete the cart item
        cartItemsRepository.deleteById(cartItemId);
    }

    private CartResponse buildCartResponse(Cart cart) {
        List<CartItemsReponse> items = cartItemsRepository.findByCartId(cart.getId()).stream()
                .map(cartItemMapper::toResponse)
                .toList();
        return cartMapper.toResponse(cart, items);
    }

}
