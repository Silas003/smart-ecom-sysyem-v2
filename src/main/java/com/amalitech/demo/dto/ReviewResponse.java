package com.amalitech.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ReviewResponse {
    private Long id;
    private Long productId;
    private Long userId;
    private Integer rating;
    private String description;
}
