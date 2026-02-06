package com.amalitech.demo.services.interfaces;

import com.amalitech.demo.dto.request.ReviewRequest;
import com.amalitech.demo.dto.response.ReviewResponse;
import com.amalitech.demo.models.Reviews;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReviewsServiceInterface {
    List<ReviewResponse> getAllReviews();


    ReviewResponse createReview(ReviewRequest request);

    ReviewResponse getReview(Long id);

    List<ReviewResponse> getReviewsByProduct(Long productId);

    List<ReviewResponse> getReviewsByUser(Long userId);


    void deleteReview(Long id);

    default ReviewResponse toResponse(Reviews r) {
        ReviewResponse resp = new ReviewResponse();
        resp.setId(r.getId());
        resp.setProductId(r.getProduct().getId());
        // anonymize reviewer display to prevent exposing raw user id or email
        String display = r.getUser().getUsername();
        System.out.println("display: " + display);
        if (display == null || display.isBlank()) {
            display = "Anonymous";
        }
        resp.setReviewerDisplay(display);
        resp.setRating(r.getRating());
        resp.setDescription(r.getDescription());
        return resp;
    }
}
