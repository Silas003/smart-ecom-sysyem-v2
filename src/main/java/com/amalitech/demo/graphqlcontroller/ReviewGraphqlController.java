package com.amalitech.demo.graphqlcontroller;

import com.amalitech.demo.dto.request.ReviewRequest;
import com.amalitech.demo.dto.response.ReviewResponse;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.models.User;
import com.amalitech.demo.services.ProductService;
import com.amalitech.demo.services.ReviewsService;
import com.amalitech.demo.services.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class  ReviewGraphqlController {

    private final ReviewsService reviewsService;
    private final UserService userService;
    private  final ProductService productService;
    public ReviewGraphqlController(ReviewsService reviewsService, UserService userService, ProductService productService) {
        this.reviewsService = reviewsService;
            this.userService = userService;
            this.productService = productService;
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

    @QueryMapping
    public List<ReviewResponse> reviewsByUser(@Argument Long userId) {
        return reviewsService.getReviewsByUser(userId);
    }

    @QueryMapping
    public List<ReviewResponse> reviewsByProduct(@Argument Long productId) {
        return reviewsService.getReviewsByProduct(productId);
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

    @SchemaMapping(typeName = "Review", field = "product")
    public Product product(ReviewResponse review) {
        return productService.getProductById(review.getProductId());
    }

    @SchemaMapping(typeName = "Review", field = "user")
    public User user(ReviewResponse review) {
        return null;
    }
}
