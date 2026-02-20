package com.amalitech.demo.restcontroller;


import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.dto.request.OrderRequest;
import com.amalitech.demo.dto.request.UpdateOrderRequest;
import com.amalitech.demo.dto.response.OrderResponse;
import com.amalitech.demo.services.interfaces.OrderServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@AllArgsConstructor
@Tag(name = "Order management", description = "Endpoints to streamline order processing")
public class OrderManagementController {
    private OrderServiceInterface orderService;

    // Ownership is enforced in OrderService; here we only handle routing and role guards

    // Customer can view their own orders; admins can view any user's orders
    @PreAuthorize("hasAnyRole('admin','customer')")
    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(method = "GET", tags = "user Orders", description = "Get orders by userId")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "User orders retrieved", content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderResponse.class)))), @ApiResponse(responseCode = "404", description = "User not found")})
    public ResponseDto<List<OrderResponse>> getOrdersByUserId(@Parameter(description = "ID of the user", required = true) @PathVariable @Valid Long userId) {
        List<OrderResponse> orders = orderService.getOrderByUserId(userId);
        return new ResponseDto<>(HttpStatus.OK, "user orders retrieved", orders);
    }

    // Customer can view their own order; admins can view any
    @PreAuthorize("hasAnyRole('admin','customer')")
    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Order retrieved", content = @Content(schema = @Schema(implementation = OrderResponse.class))), @ApiResponse(responseCode = "404", description = "Order not found")})
    public ResponseDto<OrderResponse> getOrderById(@Parameter(description = "ID of the order to retrieve", required = true) @PathVariable Long orderId) {
        OrderResponse orderResponse = orderService.getOrderById(orderId);
        return new ResponseDto<>(HttpStatus.OK, "order retrieved", orderResponse);
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Orders retrieved", content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderResponse.class))))})
    public ResponseDto<Page<OrderResponse>> getAllOrder(@PageableDefault(size = 10, sort = "totalAmount", direction = Sort.Direction.DESC) Pageable pageable, @RequestParam(required = false) Long userId, @RequestParam(required = false) com.amalitech.demo.dto.OrderStatus status, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        Page<OrderResponse> orders = orderService.getAllOrders(pageable, userId, status, start, end);
        return new ResponseDto<>(HttpStatus.OK, "orders retrieved", orders);
    }

    // Admin can delete any order; potentially customer could cancel own in future
    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Order deleted"), @ApiResponse(responseCode = "404", description = "Order not found")})
    public ResponseEntity<Void> deleteOrder(@Parameter(description = "ID of the order to delete", required = true) @PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Admin (or seller) can update order status; customers normally can't arbitrarily change it
    @PreAuthorize("hasAnyRole('admin','seller')")
    @PatchMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Order updated", content = @Content(schema = @Schema(implementation = OrderResponse.class))), @ApiResponse(responseCode = "404", description = "Order not found"), @ApiResponse(responseCode = "400", description = "Validation error")})
    public ResponseDto<OrderResponse> updateOrderStatus(@Parameter(description = "ID of the order to update", required = true) @PathVariable Long orderId, @RequestBody @Valid UpdateOrderRequest request) {
        return new ResponseDto<>(HttpStatus.OK, "order updated", orderService.updateOrderStatus(orderId, request.status()));
    }

    // Customers create orders; admin could also for support
    @PreAuthorize("hasAnyRole('admin','customer')")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create order", description = "Create a new order")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Order created", content = @Content(schema = @Schema(implementation = OrderResponse.class))), @ApiResponse(responseCode = "400", description = "Validation error")})
    public ResponseDto<OrderResponse> createOrder(@RequestBody @Valid OrderRequest request) {
        OrderResponse resp = orderService.createOrder(request);
        return new ResponseDto<>(HttpStatus.CREATED, "order created", resp);
    }

    // Customer can view their own orders within a date range; admins can view any user's orders
    @PreAuthorize("hasAnyRole('admin','customer')")
    @GetMapping("/user/{userId}/history")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user orders within period", description = "Fetch a user's orders between start and end timestamps using a native SQL query")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "User order history retrieved", content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderResponse.class)))), @ApiResponse(responseCode = "404", description = "User or orders not found")})
    public ResponseDto<Page<OrderResponse>> getUserOrdersWithinPeriod(@Parameter(description = "ID of the user", required = true) @PathVariable @Valid Long userId, @Parameter(description = "Start of the period (ISO-8601)", required = true) @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start, @Parameter(description = "End of the period (ISO-8601)", required = true) @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end, @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<OrderResponse> page = orderService.getUserOrdersWithinPeriod(userId, start, end, pageable);
        return new ResponseDto<>(HttpStatus.OK, "user order history retrieved", page);
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/reports/revenue")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get total revenue", description = "Calculate total revenue from delivered orders within a period")
    public ResponseDto<Double> getTotalRevenue(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        Double total = orderService.getTotalRevenue(start, end);
        return new ResponseDto<>(HttpStatus.OK, "total revenue retrieved", total);
    }

}
