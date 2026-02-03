package com.amalitech.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Category request payload")
public class CategoryRequest {
    @NotBlank(message = "Category name cannot be blank")
    @Schema(description = "Name of the category", example = "Electronics")
    private String name;
}
