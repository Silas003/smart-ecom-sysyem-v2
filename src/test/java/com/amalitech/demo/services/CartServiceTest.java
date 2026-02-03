package com.amalitech.demo.services;

import com.amalitech.demo.dao.interfaces.CartDao;
import com.amalitech.demo.dao.interfaces.CartItemsDao;
import com.amalitech.demo.dao.interfaces.ProductDao;
import com.amalitech.demo.dao.interfaces.UserDao;
import com.amalitech.demo.dto.CartStatus;
import com.amalitech.demo.dto.response.CartItemsReponse;
import com.amalitech.demo.dto.response.CartResponse;
import com.amalitech.demo.mapper.CartItemMapper;
import com.amalitech.demo.mapper.CartMapper;
import com.amalitech.demo.models.Cart;
import com.amalitech.demo.models.CartItems;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.models.User;
import com.amalitech.demo.utils.Sorter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartDao cartDao;

    @Mock
    private UserDao userDao;

    @Mock
    private CartItemsDao cartItemsDao;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private ProductDao productDao;

    @Mock
    private CartItemMapper cartItemMapper;

    @InjectMocks
    private CartService cartService;

    @Test
    void createCart_whenUserHasNoCart_createsAndReturns() {
        User user = new User(); user.setId(1L);
        when(userDao.findById(1L)).thenReturn(Optional.of(user));
        when(cartDao.existsByUserIdAndStatus(1L, CartStatus.active)).thenReturn(false);
        Cart cart = new Cart(user, "active"); cart.setId(10L);
        when(cartDao.save(any())).thenReturn(10L);
        when(cartMapper.toResponse(any())).thenReturn(new CartResponse(10L,1L,"active"));

        CartResponse resp = cartService.createCart(1L);
        assertNotNull(resp);
        assertEquals(10L, resp.id());
        verify(cartDao, times(1)).save(any());
    }

    @Test
    void createCart_existing_returnsExisting() {
        User user = new User(); user.setId(1L);
        Cart cart = new Cart(user, "active"); cart.setId(20L);
        when(userDao.findById(1L)).thenReturn(Optional.of(user));
        when(cartDao.existsByUserIdAndStatus(1L, CartStatus.active)).thenReturn(true);
        when(cartDao.findByUserIdAndStatus(1L, CartStatus.active)).thenReturn(Optional.of(cart));
        when(cartMapper.toResponse(cart)).thenReturn(new CartResponse(20L,1L,"active"));

        CartResponse resp = cartService.createCart(1L);
        assertNotNull(resp);
        assertEquals(20L, resp.id());
        verify(cartDao, never()).save(any());
    }

    @Test
    void addItemToCart_newItem_savesAndReturns() {
        User user = new User(); user.setId(1L);
        Cart cart = new Cart(user, "active"); cart.setId(5L);
        Product product = new Product(); product.setId(3L); product.setPrice(2.0);
        when(cartDao.findByUserIdAndStatus(1L, CartStatus.active)).thenReturn(Optional.of(cart));
        when(productDao.findById(3L)).thenReturn(Optional.of(product));
        when(cartItemsDao.findByProductIdAndCartId(3L, 5L)).thenReturn(Optional.empty());
        when(cartItemsDao.save(any())).thenReturn(99L);
        when(cartItemMapper.toResponse(any())).thenReturn(new CartItemsReponse(99L,5L,3L,2.0,4.0,2));

        CartItemsReponse resp = cartService.addItemToCart(1L, 3L, 2);
        assertNotNull(resp);
        assertEquals(99L, resp.id());
        verify(cartItemsDao, times(1)).save(any());
    }

    @Test
    void addItemToCart_existingItem_updatesQuantity() {
        User user = new User(); user.setId(1L);
        Cart cart = new Cart(user, "active"); cart.setId(5L);
        Product product = new Product(); product.setId(3L); product.setPrice(2.0);
        CartItems existing = new CartItems(); existing.setId(7L); existing.setQuantity(1);
        when(cartDao.findByUserIdAndStatus(1L, CartStatus.active)).thenReturn(Optional.of(cart));
        when(productDao.findById(3L)).thenReturn(Optional.of(product));
        when(cartItemsDao.findByProductIdAndCartId(3L, 5L)).thenReturn(Optional.of(existing));
        when(cartItemMapper.toResponse(existing)).thenReturn(new CartItemsReponse(7L,5L,3L,2.0,6.0,3));

        CartItemsReponse resp = cartService.addItemToCart(1L, 3L, 2);
        assertNotNull(resp);
        assertEquals(7L, resp.id());
        verify(cartItemsDao, times(1)).update(existing);
    }

    @Test
    void updateCartStatus_updatesAndReturns() {
        Cart cart = new Cart(new User(), "active"); cart.setId(11L);
        when(cartDao.findByUserIdAndStatus(11L, CartStatus.active)).thenReturn(Optional.of(cart));
        when(cartMapper.toResponse(cart)).thenReturn(new CartResponse(11L,11L,"deactivated"));
        cartService.updateCartStatus(11L, CartStatus.deactivated);
        verify(cartDao, times(1)).update(cart);
    }
}
