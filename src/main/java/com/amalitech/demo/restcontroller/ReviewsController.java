package com.amalitech.demo.restcontroller;

import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.dto.ReviewRequest;
import com.amalitech.demo.dto.ReviewResponse;
import com.amalitech.demo.models.Reviews;
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
    public ResponseEntity<ResponseDto> getAllReviews( ){
        List<Reviews> reviews = reviewsService.getAllReviews();
        ResponseDto responseDto = new ResponseDto(HttpStatus.OK,"reviews retrieved",reviews);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    @PostMapping("/")
    public ResponseEntity<ResponseDto> createReview(@RequestBody @Valid ReviewRequest request, @RequestParam Long userId){
        Reviews review = reviewsService.createReview(request, userId);
        ResponseDto responseDto = new ResponseDto(HttpStatus.CREATED,"review created",review);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> getReview(@PathVariable Long id){
        Reviews resp = reviewsService.getReview(id);
        ResponseDto responseDto = new ResponseDto(HttpStatus.OK,"reviews retrieved",resp);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{productId}/products")
    public ResponseEntity<ResponseDto> getReviewsByProduct(@PathVariable Long productId){
        List<Reviews> resp = reviewsService.getReviewsByProduct(productId);
        ResponseDto responseDto = new ResponseDto(HttpStatus.OK,"product reviews retrieved",resp);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{userId}/users/")
    public ResponseEntity<ResponseDto> getReviewsByUser(@PathVariable Long userId){
        List<Reviews> resp = reviewsService.getReviewsByUser(userId);
        ResponseDto responseDto = new ResponseDto(HttpStatus.OK,"user reviews retrieved",resp);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id){
        reviewsService.deleteReview(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
