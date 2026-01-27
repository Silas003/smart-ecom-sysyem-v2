package com.amalitech.demo.restcontroller;
import com.amalitech.demo.dto.CartStatus;
import com.amalitech.demo.dto.OrderStatus;
import com.amalitech.demo.dto.request.UpdateCartStatusRquest;
import com.amalitech.demo.dto.response.CartItemsReponse;
import com.amalitech.demo.dto.response.CartResponse;
import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.services.CartService;
import com.amalitech.demo.services.interfaces.CartServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/carts")
public class CartManagementController {
    private CartServiceInterface cartService;


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

    @PostMapping("/{cartId}/add_item")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto<CartItemsReponse> addItemToCart(@PathVariable Long cartId, @RequestParam Long productId, @RequestParam int quantity) {
        CartItemsReponse cartItems = cartService.addItemToCart(cartId, productId, quantity);
        return new ResponseDto<>(HttpStatus.CREATED, "item added", cartItems);
    }

    @PatchMapping("/{cardtId}/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<CartResponse> updateCartStatus(@PathVariable Long userId,@RequestBody UpdateCartStatusRquest status){
        CartResponse cartResponse = cartService.updateCartStatus(userId,status.status());
        return new ResponseDto<>(HttpStatus.OK,"cart status updated",cartResponse);
    }


}
