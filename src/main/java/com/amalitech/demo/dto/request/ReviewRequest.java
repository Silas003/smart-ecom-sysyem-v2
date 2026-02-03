package com.amalitech.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Review request payload")
public class ReviewRequest {

    @NotNull
    @Schema(description = "Product id being reviewed", example = "12")
    private Long productId;

    @NotNull
    @Min(1)
    @Max(10)
    @Schema(description = "Rating between 1 and 10", example = "8")
    private Integer rating;

    @Size(max = 2000)
    @Schema(description = "Optional review text", example = "Great product, works as expected")
    private String description;


}
