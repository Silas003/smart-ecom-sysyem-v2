package com.amalitech.demo.restcontroller;

import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.dto.ReviewRequest;
import com.amalitech.demo.dto.ReviewResponse;
import com.amalitech.demo.services.ReviewsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@AllArgsConstructor
public class ReviewsController {
    private final ReviewsService reviewsService;


    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all reviews", description = "Retrieve all reviews")
    public ResponseDto<List<ReviewResponse>> getAllReviews( ){
        List<ReviewResponse> reviews = reviewsService.getAllReviews();
        return new ResponseDto<>(HttpStatus.OK,"reviews retrieved",reviews);

    }
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create review", description = "Create a new review for a product. User is inferred from X-User-Id header")
    public ResponseDto<ReviewResponse> createReview(@RequestBody @Valid ReviewRequest request, @RequestHeader("X-User-Id") Long userId){
        ReviewResponse review = reviewsService.createReview(request, userId);
        return  new ResponseDto<>(HttpStatus.CREATED,"review created",review);
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get review", description = "Retrieve a single review by id")
    public ResponseDto<ReviewResponse> getReview(@PathVariable Long id){
        ReviewResponse resp = reviewsService.getReview(id);
        return new ResponseDto<>(HttpStatus.OK,"reviews retrieved",resp);

    }

    @GetMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get reviews by product", description = "Retrieve reviews for a specific product")
    public ResponseDto<List<ReviewResponse>> getReviewsByProduct(@PathVariable Long productId){
        List<ReviewResponse> resp = reviewsService.getReviewsByProduct(productId);
        return new ResponseDto<>(HttpStatus.OK,"product reviews retrieved",resp);

    }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get reviews by user", description = "Retrieve reviews written by a specific user")
    public ResponseDto<List<ReviewResponse>> getReviewsByUser(@PathVariable Long userId){
        List<ReviewResponse> resp = reviewsService.getReviewsByUser(userId);
        return new ResponseDto<>(HttpStatus.OK,"user reviews retrieved",resp);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deleteReview(@PathVariable Long id){
        reviewsService.deleteReview(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
