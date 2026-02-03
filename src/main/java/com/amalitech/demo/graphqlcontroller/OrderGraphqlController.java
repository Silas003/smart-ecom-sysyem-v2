package com.amalitech.demo.graphqlcontroller;

import com.amalitech.demo.dto.request.OrderRequest;
import com.amalitech.demo.dto.response.OrderResponse;
import com.amalitech.demo.services.OrderService;
import com.amalitech.demo.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderResponse.class))))
    })
    public List<OrderResponse> orders(@Argument Integer page, @Argument Integer size) {
        return orderService.getAllOrders(PageRequest.of(page, size)).getContent();
    }

    @QueryMapping
    @Operation(summary = "Get order by id (GraphQL)", description = "Retrieve a single order by id via GraphQL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order retrieved",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public OrderResponse orderById(@Argument Long id) {
        return orderService.getOrderById(id);
    }

    @MutationMapping
    @Operation(summary = "Create order (GraphQL)", description = "Create a new order via GraphQL mutation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public OrderResponse createOrder(@Argument OrderRequest input) {
        return orderService.createOrder(input);
    }

    @MutationMapping
    @Operation(summary = "Update order status (GraphQL)", description = "Update an order status via GraphQL mutation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public OrderResponse updateOrderStatus(@Argument Long id, @Argument com.amalitech.demo.dto.OrderStatus status) {
        return orderService.updateOrderStatus(id, status);
    }



}
