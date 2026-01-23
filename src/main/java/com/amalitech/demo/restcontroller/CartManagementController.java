package com.amalitech.demo.restcontroller;
import com.amalitech.demo.dto.CartResponse;
import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.models.Cart;
import com.amalitech.demo.repository.CartItemRepository;
import com.amalitech.demo.repository.CartRepository;
import com.amalitech.demo.services.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartManagementController {
    private CartService cartService;


    @PostMapping("/create_cart/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto<CartResponse> createCart(@PathVariable Long userId) {
        CartResponse cart = cartService.createCart(userId);
        return new ResponseDto<>(HttpStatus.CREATED,"cart created",cart);
    }

    @GetMapping("/get_cart/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<CartResponse> getCartByUserId(@PathVariable Long userId) {
        CartResponse cart = cartService.getCartByUserId(userId);
        return new ResponseDto<>(HttpStatus.OK, "cart fetched", cart);
    }
}
