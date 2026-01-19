package com.amalitech.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryInput {
    @NotNull
    @NotBlank
    private String name;
}
