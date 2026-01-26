package com.amalitech.demo.services;

import com.amalitech.demo.dto.CartItemsReponse;
import com.amalitech.demo.dto.CartResponse;
import com.amalitech.demo.mapper.CartMapper;
import com.amalitech.demo.mapper.CartItemMapper;
import com.amalitech.demo.models.Cart;
import com.amalitech.demo.models.CartItems;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.models.User;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.repository.CartItemsRepository;
import com.amalitech.demo.repository.CartRepository;
import com.amalitech.demo.repository.ProductRepository;
import com.amalitech.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CartService {
    private CartRepository cartRepository;
    private UserRepository userRepository;
    private CartItemsRepository cartItemsRepository;
    private CartMapper cartMapper;
    private ProductRepository productRepository;
    private CartItemMapper cartItemMapper;

    public CartResponse createCart(Long userId){
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new EntityNotFoundException("User not found"));
        boolean exists  = cartRepository.existsByUserIdAndStatus(user.getId(),"active");
        System.out.println(exists);
        if(exists){
            Cart cart = cartRepository.findByUserIdAndStatus(user.getId(),"active").orElseThrow(
                    ()-> {throw new EntityNotFoundException("cart not found");}
            );
            return cartMapper.toResponse(cart);
        }
        Cart cart = cartRepository.save(new Cart(user,"active"));

        return cartMapper.toResponse(cart);
    }


    public CartResponse getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId,"active")
                .orElseThrow(()-> new EntityNotFoundException("cart not found"));
        return cartMapper.toResponse(cart);
    }

    @Transactional
    public CartItemsReponse addItemToCart(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId,"active")
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
            CartItemsReponse  cartItemsReponse= cartItemMapper.toResponse(cartItemsRepository.save(cartItems));
            return cartItemsReponse;
        }else {
            throw new EntityNotFoundException("Cart not found");
        }

    }

    public CartResponse updateCartStatus(Long cartId,String Status){
        return new CartResponse(1L,1,"checkedout");
    }
}
