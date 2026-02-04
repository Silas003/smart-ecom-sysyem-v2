package com.amalitech.demo.restcontroller;

import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.dto.request.ReviewRequest;
import com.amalitech.demo.dto.response.ReviewResponse;
import com.amalitech.demo.services.interfaces.ReviewsServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@AllArgsConstructor
@Tag(name = "Reviews", description = "Manage product reviews")
public class ReviewsController {
    private final ReviewsServiceInterface reviewsService;


    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all reviews", description = "Retrieve all reviews")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReviewResponse.class))))
    })
    public ResponseDto<List<ReviewResponse>> getAllReviews( ){
        List<ReviewResponse> reviews = reviewsService.getAllReviews();
        return new ResponseDto<>(HttpStatus.OK,"reviews retrieved",reviews);

    }
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create review", description = "Create a new review for a product. User is inferred from X-User-Id header")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review created",
                    content = @Content(schema = @Schema(implementation = ReviewResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseDto<ReviewResponse> createReview(@RequestBody @Valid ReviewRequest request, @Parameter(description = "ID of the user creating the review, from X-User-Id header", required = true) @RequestHeader("X-User-Id") Long userId){
        ReviewResponse review = reviewsService.createReview(request, userId);
        return  new ResponseDto<>(HttpStatus.CREATED,"review created",review);
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get review", description = "Retrieve a single review by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review retrieved",
                    content = @Content(schema = @Schema(implementation = ReviewResponse.class))),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    public ResponseDto<ReviewResponse> getReview(@Parameter(description = "ID of the review to retrieve", required = true) @PathVariable Long id){
        ReviewResponse resp = reviewsService.getReview(id);
        return new ResponseDto<>(HttpStatus.OK,"reviews retrieved",resp);

    }

    @GetMapping("/products/{productId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get reviews by product", description = "Retrieve reviews for a specific product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product reviews retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReviewResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseDto<List<ReviewResponse>> getReviewsByProduct(@Parameter(description = "ID of the product", required = true) @PathVariable Long productId){
        List<ReviewResponse> resp = reviewsService.getReviewsByProduct(productId);
        return new ResponseDto<>(HttpStatus.OK,"product reviews retrieved",resp);

    }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get reviews by user", description = "Retrieve reviews written by a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User reviews retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReviewResponse.class)))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseDto<List<ReviewResponse>> getReviewsByUser(@Parameter(description = "ID of the user", required = true) @PathVariable Long userId){
        List<ReviewResponse> resp = reviewsService.getReviewsByUser(userId);
        return new ResponseDto<>(HttpStatus.OK,"user reviews retrieved",resp);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete review", description = "Delete a review by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Review deleted"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    public ResponseEntity<Void> deleteReview(@Parameter(description = "ID of the review to delete", required = true) @PathVariable Long id){
        reviewsService.deleteReview(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
