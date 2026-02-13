package com.amalitech.demo.restcontroller;

import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.dto.request.AddItemToCartRequest;
import com.amalitech.demo.dto.request.UpdateCartStatusRquest;
import com.amalitech.demo.dto.response.CartItemsReponse;
import com.amalitech.demo.dto.response.CartResponse;
import com.amalitech.demo.services.interfaces.CartServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/carts")
@Tag(name = "Carts", description = "Shopping cart management")
public class CartManagementController {
    private final CartServiceInterface cartService;

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_admin"));
    }

    // Customers manage their own carts; admin can also inspect/create if needed
    @PreAuthorize("hasAnyRole('customer','admin')")
    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get or create cart",
        description = "Retrieve the active cart for a user, or create one if it doesn't exist"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart retrieved or created successfully",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user ID"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseDto<CartResponse> getOrCreateCart(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable @Positive Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAdmin(auth)) {
            // TODO: enforce that userId matches the authenticated user when userId mapping is available
        }
        CartResponse cart = cartService.createCart(userId);
        return new ResponseDto<>(HttpStatus.OK, "Cart retrieved successfully", cart);
    }

    @PreAuthorize("hasAnyRole('customer','admin')")
    @PostMapping("/users/{userId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Add item to cart",
        description = "Add a product to user's cart or update quantity if already exists. Validates stock availability."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item added to cart successfully",
                    content = @Content(schema = @Schema(implementation = CartItemsReponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient stock"),
            @ApiResponse(responseCode = "404", description = "Cart, user, or product not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseDto<CartItemsReponse> addItemToCart(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable @Positive Long userId,
            @Parameter(description = "Product and quantity to add", required = true)
            @Valid @RequestBody AddItemToCartRequest request) {

        CartItemsReponse cartItems = cartService.addItemToCart(
                userId,
                request.productId(),
                request.quantity()
        );

        return new ResponseDto<>(HttpStatus.CREATED, "Item added to cart successfully", cartItems);
    }

    @PreAuthorize("hasAnyRole('customer','admin')")
    @DeleteMapping("/users/{userId}/items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Remove item from cart",
        description = "Remove a specific item from the user's cart. Validates ownership before removal."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item removed successfully (no content)"),
            @ApiResponse(responseCode = "400", description = "Invalid request or unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Cart item not found"),
            @ApiResponse(responseCode = "409", description = "Cannot remove item from non-active cart"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseDto<Void> removeItemFromCart(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable @Positive Long userId,
            @Parameter(description = "ID of the cart item to remove", required = true)
            @PathVariable @Positive Long cartItemId) {

        cartService.removeItemFromCart(userId, cartItemId);

        return new ResponseDto<>(HttpStatus.NO_CONTENT, "Item removed from cart successfully", null);
    }

    // Admin can adjust cart status (e.g., after order processing); customer typically cannot
    @PreAuthorize("hasAnyRole('admin','customer')")
    @PatchMapping("/{cartId}/status")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Update cart status",
        description = "Update the status of a cart (e.g., active, abandoned, completed)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart status updated successfully",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid status value"),
            @ApiResponse(responseCode = "404", description = "Cart not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseDto<CartResponse> updateCartStatus(
            @Parameter(description = "ID of the cart", required = true)
            @PathVariable @Positive Long cartId,
            @Parameter(description = "New cart status", required = true)
            @Valid @RequestBody UpdateCartStatusRquest status) {
        CartResponse cartResponse = cartService.updateCartStatus(cartId, status.status());
        return new ResponseDto<>(HttpStatus.OK, "Cart status updated successfully", null);
    }

    @PreAuthorize("hasAnyRole('customer','admin')")
    @DeleteMapping("/users/{userId}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Clear all items from cart",
        description = "Bulk remove all items from the user's active cart. Cart record is kept but becomes empty."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cart cleared successfully (no content)"),
            @ApiResponse(responseCode = "400", description = "Invalid request or unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Active cart not found for user"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseDto<Void> clearCart(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable @Positive Long userId) {

        cartService.clearCart(userId);
        return new ResponseDto<>(HttpStatus.NO_CONTENT, "Cart cleared successfully", null);
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/abandoned")
    @Operation(summary = "Get abandoned carts", description = "Retrieve active carts that haven't been updated for a specified period")
    public ResponseDto<Page<CartResponse>> getAbandonedCarts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<CartResponse> carts = cartService.getAbandonedCarts(since, pageable);
        return new ResponseDto<>(HttpStatus.OK, "abandoned carts retrieved", carts);
    }
}
