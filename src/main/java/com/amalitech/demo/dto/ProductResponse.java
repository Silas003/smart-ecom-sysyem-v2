package com.amalitech.demo.dto;

public record ProductResponse(Long id, String name, Double price, Integer stockQuantity, Long categoryId) {
}
