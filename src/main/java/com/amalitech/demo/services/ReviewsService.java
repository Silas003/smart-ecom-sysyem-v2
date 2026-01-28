package com.amalitech.demo.services;

import com.amalitech.demo.dto.request.ReviewRequest;
import com.amalitech.demo.dto.response.ReviewResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.UserMapper;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.models.Reviews;
import com.amalitech.demo.models.User;
import com.amalitech.demo.dao.interfaces.ReviewsDao;
import com.amalitech.demo.services.interfaces.ReviewsServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewsService implements ReviewsServiceInterface {
    private final ReviewsDao reviewsDao;

    private final ProductService productService;
    private final UserService userService;

    public ReviewsService(ReviewsDao reviewsDao,ProductService productService,UserService userService,
                          UserMapper userMapper) {
        this.reviewsDao = reviewsDao;
        this.userService = userService;
        this.productService = productService;
    }

    @Override
    public List<ReviewResponse> getAllReviews() {
        return reviewsDao.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ReviewResponse createReview(ReviewRequest request, Long userId) {
        Product product = productService.getProductById(request.getProductId());
        User user = userService.getUserByIdForReview(userId);

        Reviews review = new Reviews(request.getRating(), request.getDescription(),user, product);
        Reviews saved = reviewsDao.findById(reviewsDao.save(review)).orElseThrow(() -> new EntityNotFoundException("Failed to create review"));
        return toResponse(saved);

    }

    @Override
    public ReviewResponse getReview(Long id) {
        Reviews review = reviewsDao.findById(id).orElseThrow(() -> new EntityNotFoundException("Review not found"));
        return toResponse(review);
    }

    @Override
    public List<ReviewResponse> getReviewsByProduct(Long productId) {
        productService.getProductById(productId); // validate exists
        return reviewsDao.findByProductIdOrderByIdDesc(productId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getReviewsByUser(Long userId) {
        userService.getUserByIdForReview(userId); // validate exists
        return reviewsDao.findByUserIdOrderByIdDesc(userId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteReview(Long id) {
        Reviews review = reviewsDao.findById(id).orElseThrow(() -> new EntityNotFoundException("Review not found"));
        reviewsDao.deleteById(id);
    }

    public ReviewResponse toResponse(Reviews r){
        ReviewResponse resp = new ReviewResponse();
        resp.setId(r.getId());
        resp.setDescription(r.getDescription());
        resp.setRating(r.getRating());
        resp.setProductId(r.getProduct().getId());
        resp.setReviewerDisplay(r.getUser().getUsername());
        return resp;
    }

}
