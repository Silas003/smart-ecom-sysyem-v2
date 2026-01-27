package com.amalitech.demo.graphqlcontroller;

import com.amalitech.demo.dto.request.OrderRequest;
import com.amalitech.demo.dto.response.OrderResponse;
import com.amalitech.demo.services.OrderService;
import com.amalitech.demo.services.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class OrderGraphqlController {

    private final OrderService orderService;
    private final ProductService productService;

    public OrderGraphqlController(OrderService orderService, ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }

    @QueryMapping
    public List<OrderResponse> orders(@Argument Integer page, @Argument Integer size) {
        return orderService.getAllOrders(PageRequest.of(page, size)).getContent();
    }

    @QueryMapping
    public OrderResponse orderById(@Argument Long id) {
        return orderService.getOrderById(id);
    }

    @MutationMapping
    public OrderResponse createOrder(@Argument OrderRequest input) {
        return orderService.createOrder(input);
    }

    @MutationMapping
    public OrderResponse updateOrderStatus(@Argument Long id, @Argument com.amalitech.demo.dto.OrderStatus status) {
        return orderService.updateOrderStatus(id, status);
    }



}
