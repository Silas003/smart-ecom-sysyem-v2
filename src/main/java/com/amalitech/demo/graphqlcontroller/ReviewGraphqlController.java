package com.amalitech.demo.graphqlcontroller;

import com.amalitech.demo.dto.request.ReviewRequest;
import com.amalitech.demo.dto.response.ReviewResponse;
import com.amalitech.demo.services.ReviewsService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class  ReviewGraphqlController {

    private final ReviewsService reviewsService;

    public ReviewGraphqlController(ReviewsService reviewsService) {
        this.reviewsService = reviewsService;
    }

    // Public read access to reviews
    @QueryMapping
    public List<ReviewResponse> reviews() {
        return reviewsService.getAllReviews();
    }

    @QueryMapping
    public ReviewResponse reviewById(@Argument Long id) {
        return reviewsService.getReview(id);
    }

    // Authenticated customers/admins can create reviews
    @PreAuthorize("hasAnyRole('customer','admin')")
    @MutationMapping
    public ReviewResponse createReview(@Argument("input")  ReviewRequest request, @Argument Long userId) {
        return reviewsService.createReview(request);
    }

    // Admin can delete any review
    @PreAuthorize("hasRole('admin')")
    @MutationMapping
    public Boolean deleteReview(@Argument Long id) {
        reviewsService.deleteReview(id);
        return true;
    }
}
