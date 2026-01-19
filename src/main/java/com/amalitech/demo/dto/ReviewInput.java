package com.amalitech.demo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewInput {
    @NotNull
    private Long productId;

    @NotNull
    @Min(1)
    @Max(10)
    private Integer rating;

    @Size(max = 2000)
    private String description;
}
