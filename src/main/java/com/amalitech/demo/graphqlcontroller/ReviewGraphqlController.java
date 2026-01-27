package com.amalitech.demo.graphqlcontroller;

import com.amalitech.demo.dto.request.ReviewRequest;
import com.amalitech.demo.dto.response.ReviewResponse;
import com.amalitech.demo.services.ReviewsService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class  ReviewGraphqlController {

    private final ReviewsService reviewsService;

    public ReviewGraphqlController(ReviewsService reviewsService) {
        this.reviewsService = reviewsService;
    }

    @QueryMapping
    public List<ReviewResponse> reviews() {
        return reviewsService.getAllReviews();
    }

    @QueryMapping
    public ReviewResponse reviewById(@Argument Long id) {
        return reviewsService.getReview(id);
    }

    @MutationMapping
    public ReviewResponse createReview(@Argument("input")  ReviewRequest request, @Argument Long userId) {
        return reviewsService.createReview(request, userId);
    }

    @MutationMapping
    public Boolean deleteReview(@Argument Long id) {
        reviewsService.deleteReview(id);
        return true;
    }
}
