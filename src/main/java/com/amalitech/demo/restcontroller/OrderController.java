//package com.amalitech.demo.restcontroller;
//
//import com.amalitech.demo.dto.OrderRequest;
//import com.amalitech.demo.dto.OrderResponse;
//import com.amalitech.demo.dto.ResponseDto;
//import com.amalitech.demo.services.OrderService;
//import io.swagger.v3.oas.annotations.Operation;
//import jakarta.validation.Valid;
//import lombok.AllArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/v1/orders")
//@AllArgsConstructor
//public class OrderController {
//    private final OrderService orderService;
//
//    @PostMapping("/")
//    @ResponseStatus(HttpStatus.CREATED)
//    @Operation(summary = "Create order", description = "Create a new order. User inferred from X-User-Id header")
//    public ResponseDto<OrderResponse> createOrder(@RequestHeader("X-User-Id") Long userId, @RequestBody @Valid OrderRequest request){
//        OrderResponse resp = orderService.createOrder(userId, request);
//        return new ResponseDto<>(HttpStatus.CREATED, "order created", resp);
//    }
//
//    @GetMapping("/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Get order", description = "Retrieve an order by id. Caller must be the owner or admin")
//    public ResponseDto<OrderResponse> getOrder(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id){
//        OrderResponse resp = orderService.getOrder(id);
//        if(!resp.userId().equals(userId)){
//            return new ResponseDto<>(HttpStatus.FORBIDDEN, "forbidden", null);
//        }
//        return new ResponseDto<>(HttpStatus.OK,"order retrieved",resp);
//    }
//}
