package com.amalitech.demo.services;

import com.amalitech.demo.dto.CartStatus;
import com.amalitech.demo.dto.response.CartItemsReponse;
import com.amalitech.demo.dto.response.CartResponse;
import com.amalitech.demo.mapper.CartMapper;
import com.amalitech.demo.mapper.CartItemMapper;
import com.amalitech.demo.models.Cart;
import com.amalitech.demo.models.CartItems;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.models.User;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.dao.interfaces.CartItemsDao;
import com.amalitech.demo.dao.interfaces.CartDao;
import com.amalitech.demo.dao.interfaces.ProductDao;
import com.amalitech.demo.dao.interfaces.UserDao;
import com.amalitech.demo.services.interfaces.CartServiceInterface;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CartService implements CartServiceInterface {
    private final CartDao cartDao;
    private final UserDao userDao;
    private final CartItemsDao cartItemsDao;
    private final CartMapper cartMapper;
    private final ProductDao productDao;
    private final CartItemMapper cartItemMapper;

    @Override
    public CartResponse createCart(Long userId){
        User user = userDao.findById(userId).orElseThrow(
                ()-> new EntityNotFoundException("User not found"));
        boolean exists  = cartDao.existsByUserIdAndStatus(user.getId(),CartStatus.active);
        if(exists){
            Cart cart = cartDao.findByUserIdAndStatus(user.getId(),CartStatus.active).orElseThrow(
                    ()-> {throw new EntityNotFoundException("cart not found");}
            );
            return cartMapper.toResponse(cart);
        }
        Cart cart = new Cart(user,"active");
        long id = cartDao.save(cart);
        cart.setId(id);

        return cartMapper.toResponse(cart);
    }


    @Override
    public CartResponse getCartByUserId(Long userId) {
        Cart cart = cartDao.findByUserIdAndStatus(userId,CartStatus.active)
                .orElseThrow(()-> new EntityNotFoundException("cart not found"));
        return cartMapper.toResponse(cart);
    }

    @Transactional
    @Override
    public CartItemsReponse addItemToCart(Long userId, Long productId, int quantity) {
        Cart cart = cartDao.findByUserIdAndStatus(userId,CartStatus.active)
                .orElseThrow(()-> new EntityNotFoundException("cart not found"));
        if(cart != null){
            Product product = productDao.findById(productId).orElseThrow(
                    ()-> new EntityNotFoundException("Product not found"));
            CartItems cartItems = cartItemsDao.findByProductIdAndCartId(productId,cart.getId()).orElse(null);
            if(cartItems != null){
                int newQuantity = cartItems.getQuantity() + quantity;
                cartItems.setQuantity(newQuantity);
                cartItems.setTotalPrice(newQuantity*product.getPrice());
                cartItemsDao.update(cartItems);

            }else{
                cartItems = new CartItems();
                cartItems.setCart(cart);
                cartItems.setProduct(product);
                cartItems.setQuantity(quantity);
                cartItems.setUnitPrice(product.getPrice());
                cartItems.setTotalPrice(quantity*product.getPrice());
                long newId = cartItemsDao.save(cartItems);
                cartItems.setId(newId);
            }
            return cartItemMapper.toResponse(cartItems);
        }else {
            throw new EntityNotFoundException("Cart not found");
        }

    }

    @Override
    public CartResponse updateCartStatus(Long cartId, CartStatus Status){
        Cart cart = cartDao.findByUserIdAndStatus(cartId,CartStatus.active).orElseThrow(
                ()-> new EntityNotFoundException("cart not found"));

        cart.setStatus(Status);
        cartDao.update(cart);
        return cartMapper.toResponse(cart);
    }
}
