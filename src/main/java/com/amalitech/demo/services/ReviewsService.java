package com.amalitech.demo.services;

import com.amalitech.demo.dto.ReviewRequest;
import com.amalitech.demo.dto.ReviewResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.models.Reviews;
import com.amalitech.demo.models.User;
import com.amalitech.demo.repository.ReviewsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewsService {
    private final ReviewsRepository reviewsRepository;

    private final ProductService productService;
    private final UserService userService;

    public ReviewsService(ReviewsRepository reviewsRepository,ProductService productService,UserService userService) {
        this.reviewsRepository = reviewsRepository;
        this.userService = userService;
        this.productService = productService;
    }

    public List<ReviewResponse> getAllReviews() {
        List<Reviews> reviews = reviewsRepository.findAll();
        return reviews.stream().map(this::toResponse).collect(Collectors.toList());
    }
    @Transactional
    public ReviewResponse createReview(ReviewRequest request, Long userId) {
        Product product = productService.getProductById(request.getProductId());
        User user = userService.getUserById(userId);

        Reviews review = new Reviews(request.getRating(), request.getDescription(), user, product);
        Reviews saved = reviewsRepository.save(review);

        return toResponse(saved);
    }

    public ReviewResponse getReview(Long id) {
        Reviews review = reviewsRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Review not found"));
        return toResponse(review);
    }

    public List<ReviewResponse> getReviewsByProduct(Long productId) {
        productService.getProductById(productId); // validate exists
        List<Reviews> reviews = reviewsRepository.findByProduct_IdOrderByIdDesc(productId);
        return reviews.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ReviewResponse> getReviewsByUser(Long userId) {
        userService.getUserById(userId); // validate exists
        List<Reviews> reviews = reviewsRepository.findByUser_IdOrderByIdDesc(userId);
        return reviews.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void deleteReview(Long id, Long requesterId) {
        Reviews review = reviewsRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Review not found"));
        if(!review.getUser().getId().equals(requesterId)){
            throw new IllegalArgumentException("Not allowed to delete this review");
        }
        reviewsRepository.delete(review);
    }

    private ReviewResponse toResponse(Reviews r){
        ReviewResponse resp = new ReviewResponse();
        resp.setId(r.getId());
        resp.setProductId(r.getProduct().getId());
        resp.setUserId(r.getUser().getId());
        resp.setRating(r.getRating());
        resp.setDescription(r.getDescription());
        // createdAt isn't in Reviews model; leaving null
        resp.setCreatedAt(null);
        return resp;
    }
}
