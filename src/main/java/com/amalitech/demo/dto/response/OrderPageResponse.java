package com.amalitech.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "Paged order response for GraphQL")
public class OrderPageResponse {
    @Schema(description = "Order items")
    private List<OrderResponse> items;

    @Schema(description = "Total number of orders")
    private long totalCount;
}
