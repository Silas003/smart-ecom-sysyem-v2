package com.amalitech.demo.graphqlcontroller;

import com.amalitech.demo.dto.request.OrderRequest;
import com.amalitech.demo.dto.response.OrderResponse;
import com.amalitech.demo.services.OrderService;
import com.amalitech.demo.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Tag(name = "GraphQL - Orders", description = "GraphQL queries and mutations for orders")
public class OrderGraphqlController {

    private final OrderService orderService;
    private final ProductService productService;

    public OrderGraphqlController(OrderService orderService, ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }

    @QueryMapping
    @Operation(summary = "List orders (GraphQL)", description = "List orders with pagination via GraphQL")
    public List<OrderResponse> orders(@Argument Integer page, @Argument Integer size) {
        return orderService.getAllOrders(PageRequest.of(page, size)).getContent();
    }

    @QueryMapping
    @Operation(summary = "Get order by id (GraphQL)", description = "Retrieve a single order by id via GraphQL")
    public OrderResponse orderById(@Argument Long id) {
        return orderService.getOrderById(id);
    }

    @MutationMapping
    @Operation(summary = "Create order (GraphQL)", description = "Create a new order via GraphQL mutation")
    public OrderResponse createOrder(@Argument OrderRequest input) {
        return orderService.createOrder(input);
    }

    @MutationMapping
    @Operation(summary = "Update order status (GraphQL)", description = "Update an order status via GraphQL mutation")
    public OrderResponse updateOrderStatus(@Argument Long id, @Argument com.amalitech.demo.dto.OrderStatus status) {
        return orderService.updateOrderStatus(id, status);
    }



}
