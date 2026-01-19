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

    public List<Reviews> getAllReviews() {
        List<Reviews> reviews = reviewsRepository.findAll();
        return reviews;
    }
    @Transactional
    public Reviews createReview(ReviewRequest request, Long userId) {
        Product product = productService.getProductById(request.getProductId());
        User user = userService.getUserById(userId);

        Reviews review = new Reviews(request.getRating(), request.getDescription(), user, product);
        return reviewsRepository.save(review);

    }

    public Reviews getReview(Long id) {
        Reviews review = reviewsRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Review not found"));
        return review;
    }

    public List<Reviews> getReviewsByProduct(Long productId) {
        productService.getProductById(productId); // validate exists
        List<Reviews> reviews = reviewsRepository.findByProduct_IdOrderByIdDesc(productId);
        return reviews;
    }

    public List<Reviews> getReviewsByUser(Long userId) {
        userService.getUserById(userId); // validate exists
        List<Reviews> reviews = reviewsRepository.findByUser_IdOrderByIdDesc(userId);
        return reviews;
    }

    @Transactional
    public void deleteReview(Long id) {
        Reviews review = reviewsRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Review not found"));
        reviewsRepository.delete(review);
    }


}
