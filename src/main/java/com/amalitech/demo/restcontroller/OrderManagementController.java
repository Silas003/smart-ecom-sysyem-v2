package com.amalitech.demo.restcontroller;


import com.amalitech.demo.dto.OrderRequest;
import com.amalitech.demo.dto.OrderResponse;
import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.dto.UpdateOrderRequest;
import com.amalitech.demo.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@AllArgsConstructor
@Tag(name = "Order management",description = "Endpoints to streamline order processing")
public class OrderManagementController {
    private OrderService orderService;

    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(method = "GET",tags = "user Orders",description = "Get orders by userId")
    public ResponseDto<List<OrderResponse>> getOrdersByUserId(@PathVariable @Valid Long userId){
        List<OrderResponse> orders = orderService.getOrderByUserId(userId);
        return new ResponseDto<>(HttpStatus.OK,"user orders retrieved",orders);
    }

    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<OrderResponse> getOrderById(@PathVariable Long orderId){
        OrderResponse orderResponse = orderService.getOrderById(orderId);
        return new ResponseDto<>(HttpStatus.OK,"order retrieved",orderResponse);
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<Page<OrderResponse>> getAllOrder(@PageableDefault(
            size = 10,sort = "totalAmount",direction = Sort.Direction.DESC
    )Pageable pageable){
        Page<OrderResponse> orders  = orderService.getAllOrders(pageable);
        return new ResponseDto<>(HttpStatus.OK,"orders retrieved",orders);
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity deleteOrder(@PathVariable Long orderId){
        orderService.deleteOrder(orderId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<OrderResponse> updateOrderStatus(@PathVariable Long orderId,
                                           @RequestBody @Valid UpdateOrderRequest request) {
        return new ResponseDto<>(HttpStatus.OK,"order updated",orderService.updateOrderStatus(orderId, request.status()));
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create order", description = "Create a new order")
    public ResponseDto<OrderResponse> createOrder( @RequestBody @Valid OrderRequest request){
        OrderResponse resp = orderService.createOrder( request);
        return new ResponseDto<>(HttpStatus.CREATED, "order created", resp);
    }

}
