package com.amalitech.demo.services;

import com.amalitech.demo.dto.request.ReviewRequest;
import com.amalitech.demo.dto.response.ReviewResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.models.Reviews;
import com.amalitech.demo.models.User;
import com.amalitech.demo.repository.ReviewsRepository;
import com.amalitech.demo.services.interfaces.ReviewsServiceInterface;
import com.amalitech.demo.services.specification.ReviewSpecification;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewsService implements ReviewsServiceInterface {
    private final ReviewsRepository reviewsRepository;
    private final ProductService productService;
    private final UserService userService;

    public ReviewsService(ReviewsRepository reviewsRepository, ProductService productService, UserService userService) {
        this.reviewsRepository = reviewsRepository;
        this.userService = userService;
        this.productService = productService;
    }

    @Override
    @Cacheable(value = "reviews", keyGenerator = "reviewKeyGenerator")
    public List<ReviewResponse> getAllReviews(Long productId, Long userId) {
        Specification<Reviews> spec = Specification.anyOf(ReviewSpecification.hasProductId(productId)).and(ReviewSpecification.hasUserId(userId));

        List<Reviews> list = reviewsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "id"));
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "review", key = "#id"), @CacheEvict(value = "reviewsByProduct", allEntries = true), @CacheEvict(value = "reviewsByUser", allEntries = true), @CacheEvict(value = "averageRating", allEntries = true)})
    public ReviewResponse createReview(ReviewRequest request) {
        Product product = productService.getProductById(request.getProductId());
        User user = userService.getUserByIdForReview(request.getUserId());

        Reviews review = new Reviews(request.getRating(), request.getDescription(), user, product);
        Reviews saved = reviewsRepository.save(review);
        return toResponse(saved);

    }

    @Override
    @Cacheable(value = "review", key = "#id")
    public ReviewResponse getReview(Long id) {
        Reviews review = reviewsRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Review not found"));
        return toResponse(review);
    }

    @Override
    @Cacheable(value = "reviewsByProduct", key = "#productId")
    public List<ReviewResponse> getReviewsByProduct(Long productId) {
        return getAllReviews(productId, null);
    }

    @Override
    @Cacheable(value = "reviewsByUser", key = "#userId")
    public List<ReviewResponse> getReviewsByUser(Long userId) {
        return getAllReviews(null, userId);
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "review", key = "#id"), @CacheEvict(value = "reviewsByProduct", allEntries = true), @CacheEvict(value = "reviewsByUser", allEntries = true), @CacheEvict(value = "averageRating", allEntries = true)})
    public void deleteReview(Long id) {
        if (reviewsRepository.findById(id).isEmpty()) throw new EntityNotFoundException("Review not found");
        reviewsRepository.deleteById(id);
    }

    @CachePut(value = "averageRating", key = "#productId")
    @Override
    public Double getAverageRating(Long productId) {
        return reviewsRepository.getAverageRatingByProductId(productId);
    }

    public ReviewResponse toResponse(Reviews r) {
        ReviewResponse resp = new ReviewResponse();
        resp.setId(r.getId());
        resp.setDescription(r.getDescription());
        resp.setRating(r.getRating());
        resp.setProductId(r.getProduct().getId());
        resp.setReviewerDisplay(r.getUser().getUsername());
        resp.setCreatedAt(r.getCreatedAt());
        return resp;
    }

}
