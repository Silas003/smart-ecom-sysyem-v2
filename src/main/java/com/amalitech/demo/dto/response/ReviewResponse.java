package com.amalitech.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;


@Schema(description = "Review response model")
@Data
public class ReviewResponse {
    private Long id;
    private Long productId;
    private String reviewerDisplay; // anonymized display name
    private Integer rating;
    private String description;
    private LocalDateTime createdAt;
}
