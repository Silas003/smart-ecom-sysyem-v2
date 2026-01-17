package com.amalitech.demo.controller;

import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.dto.ReviewRequest;
import com.amalitech.demo.dto.ReviewResponse;
import com.amalitech.demo.models.Reviews;
import com.amalitech.demo.services.ReviewsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@Tag(name = "Reviews", description = "APIs to manage product reviews")
public class ReviewsController {
    private final ReviewsService reviewsService;

    public ReviewsController(ReviewsService reviewsService) {
        this.reviewsService = reviewsService;
    }


    @GetMapping("/")
    @Operation(summary = "Get all reviews", description = "Retrieve all reviews")
    public ResponseEntity<ResponseDto> getAllReviews( ){
        List<Reviews> reviews = reviewsService.getAllReviews();
        ResponseDto responseDto = new ResponseDto(HttpStatus.OK,"reviews retrieved",reviews);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    @PostMapping("/")
    @Operation(summary = "Create review", description = "Create a new review for a product")
    public ResponseEntity<ResponseDto> createReview(@RequestBody @Valid Reviews reviews, @RequestParam Long userId){
        Reviews review = reviewsService.createReview(reviews, userId);
        ResponseDto responseDto = new ResponseDto(HttpStatus.CREATED,"review created",review);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get review", description = "Retrieve a single review by id")
    public ResponseEntity<ResponseDto> getReview(@PathVariable Long id){
        Reviews resp = reviewsService.getReview(id);
        ResponseDto responseDto = new ResponseDto(HttpStatus.OK,"reviews retrieved",resp);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{productId}/products")
    @Operation(summary = "Get reviews by product", description = "Retrieve reviews for a specific product")
    public ResponseEntity<ResponseDto> getReviewsByProduct(@PathVariable Long productId){
        List<Reviews> resp = reviewsService.getReviewsByProduct(productId);
        ResponseDto responseDto = new ResponseDto(HttpStatus.OK,"product reviews retrieved",resp);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{userId}/users/")
    @Operation(summary = "Get reviews by user", description = "Retrieve reviews written by a specific user")
    public ResponseEntity<ResponseDto> getReviewsByUser(@PathVariable Long userId){
        List<Reviews> resp = reviewsService.getReviewsByUser(userId);
        ResponseDto responseDto = new ResponseDto(HttpStatus.OK,"user reviews retrieved",resp);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/reviews/{id}")
    @Operation(summary = "Delete review", description = "Delete a review by id (requester must be owner)")
    public ResponseEntity<ResponseDto> deleteReview(@PathVariable Long id, @RequestParam Long userId){
        reviewsService.deleteReview(id, userId);
        ResponseDto responseDto = new ResponseDto(HttpStatus.NO_CONTENT,"review deleted",null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(responseDto);
    }
}
