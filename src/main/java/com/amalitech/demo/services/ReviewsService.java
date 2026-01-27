package com.amalitech.demo.services;

import com.amalitech.demo.dto.request.ReviewRequest;
import com.amalitech.demo.dto.response.ReviewResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.UserMapper;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.models.Reviews;
import com.amalitech.demo.models.User;
import com.amalitech.demo.repository.ReviewsRepository;
import com.amalitech.demo.services.interfaces.ReviewsServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewsService implements ReviewsServiceInterface {
    private final ReviewsRepository reviewsRepository;

    private final ProductService productService;
    private final UserService userService;

    public ReviewsService(ReviewsRepository reviewsRepository,ProductService productService,UserService userService,
                          UserMapper userMapper) {
        this.reviewsRepository = reviewsRepository;
        this.userService = userService;
        this.productService = productService;
    }

    @Override
    public List<ReviewResponse> getAllReviews() {
        return reviewsRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ReviewResponse createReview(ReviewRequest request, Long userId) {
        Product product = productService.getProductById(request.getProductId());
        User user = userService.getUserByIdForReview(userId);

        Reviews review = new Reviews(request.getRating(), request.getDescription(),user, product);
        Reviews saved = reviewsRepository.save(review);
        return toResponse(saved);

    }

    @Override
    public ReviewResponse getReview(Long id) {
        Reviews review = reviewsRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Review not found"));
        return toResponse(review);
    }

    @Override
    public List<ReviewResponse> getReviewsByProduct(Long productId) {
        productService.getProductById(productId); // validate exists
        return reviewsRepository.findByProduct_IdOrderByIdDesc(productId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getReviewsByUser(Long userId) {
        userService.getUserByIdForReview(userId); // validate exists
        return reviewsRepository.findByUser_IdOrderByIdDesc(userId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteReview(Long id) {
        Reviews review = reviewsRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Review not found"));
        reviewsRepository.delete(review);
    }

}
