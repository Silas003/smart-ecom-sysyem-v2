package com.amalitech.demo.services;

import com.amalitech.demo.dto.CartStatus;
import com.amalitech.demo.dto.response.CartItemsReponse;
import com.amalitech.demo.dto.response.CartResponse;
import com.amalitech.demo.mapper.CartItemMapper;
import com.amalitech.demo.mapper.CartMapper;
import com.amalitech.demo.models.*;
import com.amalitech.demo.repository.CartItemsRepository;
import com.amalitech.demo.repository.CartRepository;
import com.amalitech.demo.repository.InventoryRepository;
import com.amalitech.demo.repository.ProductRepository;
import com.amalitech.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartItemsRepository cartItemsRepository;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartItemMapper cartItemMapper;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void createCart_whenUserHasNoCart_createsAndReturns() {
        User user = new User(); user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.existsByUserIdAndStatus(1L, CartStatus.active)).thenReturn(false);
        Cart cart = new Cart(user); cart.setId(10L);
        when(cartRepository.save(any())).thenReturn(cart);
        when(cartItemsRepository.findByCartId(10L)).thenReturn(List.of());
        when(cartMapper.toResponse(any(), anyList())).thenReturn(new CartResponse(10L,1L,"active", List.of()));

        CartResponse resp = cartService.createCart(1L);
        assertNotNull(resp);
        assertEquals(10L, resp.id());
        verify(cartRepository, times(1)).save(any());
    }

    @Test
    void createCart_existing_returnsExisting() {
        User user = new User(); user.setId(1L);
        Cart cart = new Cart(user); cart.setId(20L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.existsByUserIdAndStatus(1L, CartStatus.active)).thenReturn(true);
        when(cartRepository.findByUserIdAndStatus(1L, CartStatus.active)).thenReturn(Optional.of(cart));
        when(cartItemsRepository.findByCartId(20L)).thenReturn(List.of());
        when(cartMapper.toResponse(any(), anyList())).thenReturn(new CartResponse(20L,1L,"active", List.of()));

        CartResponse resp = cartService.createCart(1L);
        assertNotNull(resp);
        assertEquals(20L, resp.id());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void addItemToCart_newItem_savesAndReturns() {
        User user = new User(); user.setId(1L);
        Cart cart = new Cart(user); cart.setId(5L);
        Product product = new Product(); product.setId(3L); product.setPrice(2.0);
        Inventory inventory = new Inventory(); inventory.setProduct(product); inventory.setStockQuantity(10);
        when(cartRepository.findByUserIdAndStatus(1L, CartStatus.active)).thenReturn(Optional.of(cart));
        when(productRepository.findById(3L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(3L)).thenReturn(Optional.of(inventory));
        when(cartItemsRepository.findByProductIdAndCartId(3L, 5L)).thenReturn(Optional.empty());
        CartItems savedItem = new CartItems(); savedItem.setId(99L);
        when(cartItemsRepository.save(any())).thenReturn(savedItem);
        when(cartItemMapper.toResponse(any())).thenReturn(new CartItemsReponse(99L,5L,3L,2.0,4.0,2));

        CartItemsReponse resp = cartService.addItemToCart(1L, 3L, 2);
        assertNotNull(resp);
        assertEquals(99L, resp.id());
        verify(cartItemsRepository, times(1)).save(any());
    }

    @Test
    void addItemToCart_existingItem_updatesQuantity() {
        User user = new User(); user.setId(1L);
        Cart cart = new Cart(user); cart.setId(5L);
        Product product = new Product(); product.setId(3L); product.setPrice(2.0);
        Inventory inventory = new Inventory(); inventory.setProduct(product); inventory.setStockQuantity(10);
        CartItems existing = new CartItems(); existing.setId(7L); existing.setQuantity(1);
        when(cartRepository.findByUserIdAndStatus(1L, CartStatus.active)).thenReturn(Optional.of(cart));
        when(productRepository.findById(3L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(3L)).thenReturn(Optional.of(inventory));
        when(cartItemsRepository.findByProductIdAndCartId(3L, 5L)).thenReturn(Optional.of(existing));
        when(cartItemsRepository.save(existing)).thenReturn(existing);
        when(cartItemMapper.toResponse(existing)).thenReturn(new CartItemsReponse(7L,5L,3L,2.0,6.0,3));

        CartItemsReponse resp = cartService.addItemToCart(1L, 3L, 2);
        assertNotNull(resp);
        assertEquals(7L, resp.id());
        verify(cartItemsRepository, times(1)).save(existing);
    }

    @Test
    void updateCartStatus_updatesAndReturns() {
        Cart cart = new Cart(new User());
        cart.setId(11L);

        when(cartRepository.findById(11L))
                .thenReturn(Optional.of(cart));

        when(cartItemsRepository.findByCartId(11L))
                .thenReturn(List.of());

        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(cartMapper.toResponse(eq(cart), anyList()))
                .thenReturn(new CartResponse(
                        11L,
                        11L,
                        "checkout",
                        List.of()
                ));

        CartResponse resp = cartService.updateCartStatus(11L, CartStatus.checkout);

        assertEquals(CartStatus.checkout.name(), resp.status());
        verify(cartRepository, times(1)).save(cart);
    }

}
