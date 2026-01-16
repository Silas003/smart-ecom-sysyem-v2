package com.amalitech.demo.controller;

import com.amalitech.demo.dto.ReviewRequest;
import com.amalitech.demo.dto.ReviewResponse;
import com.amalitech.demo.services.ReviewsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewsController {
    private final ReviewsService reviewsService;

    public ReviewsController(ReviewsService reviewsService) {
        this.reviewsService = reviewsService;
    }


    @GetMapping("/")
    public ResponseEntity<List<ReviewResponse>> getAllReviews( ){
        List<ReviewResponse> resp = reviewsService.getAllReviews();
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }
    @PostMapping("/")
    public ResponseEntity<ReviewResponse> createReview(@RequestBody @Valid ReviewRequest request, @RequestParam Long userId){
        ReviewResponse resp = reviewsService.createReview(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable Long id){
        ReviewResponse resp = reviewsService.getReview(id);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{productId}/products")
    public ResponseEntity<List<ReviewResponse>> getReviewsByProduct(@PathVariable Long productId){
        List<ReviewResponse> resp = reviewsService.getReviewsByProduct(productId);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{userId}/users/")
    public ResponseEntity<List<ReviewResponse>> getReviewsByUser(@PathVariable Long userId){
        List<ReviewResponse> resp = reviewsService.getReviewsByUser(userId);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id, @RequestParam Long userId){
        reviewsService.deleteReview(id, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
