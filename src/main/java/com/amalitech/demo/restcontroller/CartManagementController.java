package com.amalitech.demo.restcontroller;
import com.amalitech.demo.dto.CartStatus;
import com.amalitech.demo.dto.OrderStatus;
import com.amalitech.demo.dto.request.UpdateCartStatusRquest;
import com.amalitech.demo.dto.response.CartItemsReponse;
import com.amalitech.demo.dto.response.CartResponse;
import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.services.CartService;
import com.amalitech.demo.services.interfaces.CartServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/carts")
@Tag(name = "Carts", description = "Shopping cart management")
public class CartManagementController {
    private CartServiceInterface cartService;


    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create cart", description = "Create a new cart for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cart created",
                    content = @Content(schema = @Schema(implementation = CartResponse.class)))
    })
    public ResponseDto<CartResponse> createCart(@Parameter(description = "ID of the user to create a cart for", required = true) @PathVariable Long userId) {
        CartResponse cart = cartService.createCart(userId);
        return new ResponseDto<>(HttpStatus.CREATED,"cart created",cart);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get cart by user", description = "Retrieve the active cart for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart fetched",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    public ResponseDto<CartResponse> getCartByUserId(@Parameter(description = "ID of the user", required = true) @PathVariable Long userId) {
        CartResponse cart = cartService.getCartByUserId(userId);
        return new ResponseDto<>(HttpStatus.OK, "cart fetched", cart);
    }

    @PostMapping("/{cartId}/add_item")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add item to cart", description = "Add a product to a user's cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item added to cart",
                    content = @Content(schema = @Schema(implementation = CartItemsReponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error or insufficient stock")
    })
    public ResponseDto<CartItemsReponse> addItemToCart(@Parameter(description = "ID of the cart", required = true) @PathVariable Long cartId, @Parameter(description = "ID of the product to add", required = true) @RequestParam Long productId, @Parameter(description = "Quantity to add", required = true) @RequestParam int quantity) {
        CartItemsReponse cartItems = cartService.addItemToCart(cartId, productId, quantity);
        return new ResponseDto<>(HttpStatus.CREATED, "item added", cartItems);
    }

    @PatchMapping("/{cardtId}/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update cart status", description = "Update the status of a user's cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart status updated",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cart or user not found")
    })
    public ResponseDto<CartResponse> updateCartStatus(@Parameter(description = "ID of the user", required = true) @PathVariable Long userId,@RequestBody UpdateCartStatusRquest status){
        CartResponse cartResponse = cartService.updateCartStatus(userId,status.status());
        return new ResponseDto<>(HttpStatus.OK,"cart status updated",cartResponse);
    }

}
