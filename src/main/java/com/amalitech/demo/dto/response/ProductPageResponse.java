package com.amalitech.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "Paged product response for GraphQL")
public class ProductPageResponse {
    @Schema(description = "Product items")
    private List<ProductResponse> items;

    @Schema(description = "Total number of products")
    private long totalCount;
}
