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
import org.springframework.cache.annotation.Cacheable;
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
    public CartResponse createCart(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        boolean exists = cartRepository.existsByUserIdAndStatus(user.getId(), CartStatus.active);
        if (exists) {
            Cart cart = cartRepository.findByUserIdAndStatus(user.getId(), CartStatus.active).orElseThrow(() -> new EntityNotFoundException("cart not found"));
            return buildCartResponse(cart);
        }
        Cart cart = cartRepository.save(new Cart(user));

        return buildCartResponse(cart);
    }

    @Cacheable(value = "activeUserCart", key = "#userId")
    @Override
    public CartResponse getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.active).orElseThrow(() -> new EntityNotFoundException("cart not found"));
        return buildCartResponse(cart);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public CartItemsReponse addItemToCart(Long userId, Long productId, int quantity) {

        Cart cart = getActiveCartOrThrow(userId);

        Product product = getProductOrThrow(productId);

        Inventory inventory = getInventoryOrThrow(productId);

        CartItems cartItem = getCartItem(cart.getId(), productId);

        int newQuantity = calculateNewQuantity(cartItem, quantity);

        validateStockAvailability(inventory, newQuantity, product.getName());

        CartItems savedCartItem = saveOrUpdateCartItem(cartItem, cart, product, newQuantity);

        return cartItemMapper.toResponse(savedCartItem);
    }


    @CachePut(value = "activeUserCart", key = "#result.id")
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public CartResponse updateCartStatus(Long cartId, CartStatus Status) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new EntityNotFoundException("cart not found"));

        cart.setStatus(Status);
        Cart saved = cartRepository.save(cart);
        return buildCartResponse(saved);
    }

    @Override
    @CacheEvict(value = "activeUserCart", allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeItemFromCart(Long userId, Long cartItemId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        CartItems cartItem = cartItemsRepository.findById(cartItemId).orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        Long cartId = cartItem.getCart().getId();

        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        if (!cart.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Cannot remove item from another user's cart");
        }

        // 3. Verify cart is active (cannot remove items from completed/abandoned carts)
        if (cart.getStatus() != CartStatus.active) {
            throw new IllegalStateException("Cannot remove items from a " + cart.getStatus() + " cart");
        }

        cartItemsRepository.deleteById(cartItemId);

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

    private Cart getActiveCartOrThrow(Long userId) {
        return cartRepository.findByUserIdAndStatus(userId, CartStatus.active).orElseThrow(() -> new EntityNotFoundException("Active cart not found for user ID: " + userId));
    }

    private Product getProductOrThrow(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));
    }

    private Inventory getInventoryOrThrow(Long productId) {
        return inventoryRepository.findByProductId(productId).orElseThrow(() -> new EntityNotFoundException("Inventory not found for product ID: " + productId));
    }

    private CartItems getCartItem(Long cartId, Long productId) {
        return cartItemsRepository.findByProductIdAndCartId(productId, cartId).orElse(null);
    }

    private int calculateNewQuantity(CartItems existingItem, int requestedQuantity) {

        if (existingItem == null) {
            return requestedQuantity;
        }

        return existingItem.getQuantity() + requestedQuantity;
    }

    private void validateStockAvailability(Inventory inventory, int requestedQuantity, String productName) {

        if (inventory.getStockQuantity() < requestedQuantity) {
            throw new IllegalArgumentException(String.format("Insufficient stock for product: %s. Available: %d, Requested: %d", productName, inventory.getStockQuantity(), requestedQuantity));
        }
    }

    private CartItems saveOrUpdateCartItem(CartItems existingItem, Cart cart, Product product, int quantity) {

        CartItems cartItem = existingItem != null ? existingItem : new CartItems();

        if (existingItem == null) {
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setUnitPrice(product.getPrice());
        }

        cartItem.setQuantity(quantity);
        cartItem.setTotalPrice(quantity * product.getPrice());

        return cartItemsRepository.save(cartItem);
    }


}
