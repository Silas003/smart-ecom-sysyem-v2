package com.amalitech.demo.services;

import com.amalitech.demo.dto.CartStatus;
import com.amalitech.demo.dto.response.CartItemsReponse;
import com.amalitech.demo.dto.response.CartResponse;
import com.amalitech.demo.mapper.CartMapper;
import com.amalitech.demo.mapper.CartItemMapper;
import com.amalitech.demo.models.*;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.repository.CartItemsRepository;
import com.amalitech.demo.repository.CartRepository;
import com.amalitech.demo.repository.ProductRepository;
import com.amalitech.demo.repository.UserRepository;
import com.amalitech.demo.services.interfaces.CartServiceInterface;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

@AllArgsConstructor
@Service
public class CartService implements CartServiceInterface {
    private CartRepository cartRepository;
    private UserRepository userRepository;
    private CartItemsRepository cartItemsRepository;
    private CartMapper cartMapper;
    private ProductRepository productRepository;
    private CartItemMapper cartItemMapper;

    @CachePut(value = "activeUserCart",key="result.id + #userId")
    @Transactional
    @Override
    public CartResponse createCart(Long userId){
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new EntityNotFoundException("User not found"));
        boolean exists  = cartRepository.existsByUserIdAndStatus(user.getId(),CartStatus.active);
        if(exists){
            Cart cart = cartRepository.findByUserIdAndStatus(user.getId(),CartStatus.active).orElseThrow(
                    ()-> {throw new EntityNotFoundException("cart not found");}
            );
            return cartMapper.toResponse(cart);
        }
        Cart cart = cartRepository.save(new Cart(user,"active"));

        return cartMapper.toResponse(cart);
    }

    @Cacheable(value = "cart",key = "#userId")
    @Override
    public CartResponse getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId,CartStatus.active)
                .orElseThrow(()-> new EntityNotFoundException("cart not found"));
        return cartMapper.toResponse(cart);
    }

    @Transactional
    @Override
    public CartItemsReponse addItemToCart(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId,CartStatus.active)
                .orElseThrow(()-> new EntityNotFoundException("cart not found"));
        if(cart != null){
            Product product = productRepository.findById(productId).orElseThrow(
                    ()-> new EntityNotFoundException("Product not found"));
            CartItems cartItems = cartItemsRepository.findByProduct_IdAndCart_Id(productId,cart.getId()).orElse(null);
            if(cartItems != null){
                int newQuantity = cartItems.getQuantity() + quantity;
                cartItems.setQuantity(newQuantity);
                cartItems.setTotalPrice(newQuantity*product.getPrice());

            }else{
                cartItems = new CartItems();
                cartItems.setCart(cart);
                cartItems.setProduct(product);
                cartItems.setQuantity(quantity);
                cartItems.setUnitPrice(product.getPrice());
                cartItems.setTotalPrice(quantity*product.getPrice());
            }
            return cartItemMapper.toResponse(cartItemsRepository.save(cartItems));
        }else {
            throw new EntityNotFoundException("Cart not found");
        }

    }

    @CachePut(value="activeUserCart",key = "#cartId + result.userId")
    @Transactional
    @Override
    public CartResponse updateCartStatus(Long cartId, CartStatus Status){
        Cart cart = cartRepository.findByUserIdAndStatus(cartId,CartStatus.active).orElseThrow(
                ()-> new EntityNotFoundException("cart not found"));

        cart.setStatus(Status);
        Cart updatedCart = cartRepository.save(cart);
        return cartMapper.toResponse(updatedCart);
    }

}
