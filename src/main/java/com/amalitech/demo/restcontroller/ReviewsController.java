package com.amalitech.demo.restcontroller;

import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.dto.ReviewRequest;
import com.amalitech.demo.dto.ReviewResponse;
import com.amalitech.demo.services.ReviewsService;
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
    public ResponseDto<List<ReviewResponse>> getAllReviews( ){
        List<ReviewResponse> reviews = reviewsService.getAllReviews();
        return new ResponseDto<>(HttpStatus.OK,"reviews retrieved",reviews);

    }
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto<ReviewResponse> createReview(@RequestBody @Valid ReviewRequest request, @RequestParam Long userId){
        ReviewResponse review = reviewsService.createReview(request, userId);
        return  new ResponseDto<>(HttpStatus.CREATED,"review created",review);
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<ReviewResponse> getReview(@PathVariable Long id){
        ReviewResponse resp = reviewsService.getReview(id);
        return new ResponseDto<>(HttpStatus.OK,"reviews retrieved",resp);

    }

    @GetMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<List<ReviewResponse>> getReviewsByProduct(@PathVariable Long productId){
        List<ReviewResponse> resp = reviewsService.getReviewsByProduct(productId);
        return new ResponseDto<>(HttpStatus.OK,"product reviews retrieved",resp);

    }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
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
