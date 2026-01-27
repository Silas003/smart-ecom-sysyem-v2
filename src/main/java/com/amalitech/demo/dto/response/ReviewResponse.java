package com.amalitech.demo.dto.response;

import lombok.Data;


@Data
public class ReviewResponse {
    private Long id;
    private Long productId;
    private String reviewerDisplay; // anonymized display name
    private Integer rating;
    private String description;
}
