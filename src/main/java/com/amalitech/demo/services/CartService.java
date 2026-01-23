package com.amalitech.demo.services;

import com.amalitech.demo.dto.CartResponse;
import com.amalitech.demo.mapper.CartMapper;
import com.amalitech.demo.mapper.UserMapper;
import com.amalitech.demo.models.Cart;
import com.amalitech.demo.models.User;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.repository.CartItemRepository;
import com.amalitech.demo.repository.CartRepository;
import com.amalitech.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CartService {
    private CartRepository cartRepository;
    private UserRepository userRepository;
    private CartItemRepository cartItemRepository;
    private CartMapper cartMapper;

    public CartResponse createCart(Long userId){
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new EntityNotFoundException("User not found"));
        boolean exists  = cartRepository.existsByUserIdAndStatus(user.getId(),"active");
        System.out.println(exists);
        if(exists){
            Cart cart = cartRepository.findByUserIdAndStatus(user.getId(),"active");
            return cartMapper.toResponse(cart);
        }
        Cart cart = cartRepository.save(new Cart(user,"active"));

        return cartMapper.toResponse(cart);
    }


    public CartResponse getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId,"active");
        return cartMapper.toResponse(cart);
    }
}
