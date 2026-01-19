package com.amalitech.demo.graphqlcontroller;

import com.amalitech.demo.dto.ReviewInput;
import com.amalitech.demo.dto.ReviewRequest;
import com.amalitech.demo.models.Reviews;
import com.amalitech.demo.services.ReviewsService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ReviewGraphqlController {

    private final ReviewsService reviewsService;

    public ReviewGraphqlController(ReviewsService reviewsService) {
        this.reviewsService = reviewsService;
    }

    @QueryMapping
    public List<Reviews> reviews() {
        return reviewsService.getAllReviews();
    }

    @QueryMapping
    public Reviews reviewById(@Argument Long id) {
        return reviewsService.getReview(id);
    }

    @MutationMapping
    public Reviews createReview(@Argument ReviewInput input, @Argument Long userId) {
        ReviewRequest req = new ReviewRequest();
        req.setRating(input.getRating());
        req.setDescription(input.getDescription());
        req.setProductId(input.getProductId());
        return reviewsService.createReview(req, userId);
    }

    @MutationMapping
    public Boolean deleteReview(@Argument Long id) {
        reviewsService.deleteReview(id);
        return true;
    }
}
