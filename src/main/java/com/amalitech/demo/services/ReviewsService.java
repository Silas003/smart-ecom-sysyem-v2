package com.amalitech.demo.services;

import com.amalitech.demo.dto.request.ReviewRequest;
import com.amalitech.demo.dto.response.ReviewResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.models.Reviews;
import com.amalitech.demo.models.User;
import com.amalitech.demo.repository.ReviewsRepository;
import com.amalitech.demo.services.interfaces.ReviewsServiceInterface;
import com.amalitech.demo.utils.Sorter;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewsService implements ReviewsServiceInterface {
    private final ReviewsRepository reviewsRepository;

    private final ProductService productService;
    private final UserService userService;
    private final Sorter<Reviews> sorter;

    public ReviewsService(ReviewsRepository reviewsRepository, ProductService productService, UserService userService,
                          Sorter<Reviews> sorter) {
        this.reviewsRepository = reviewsRepository;
        this.userService = userService;
        this.productService = productService;
        this.sorter = sorter;
    }

    @Override
    public List<ReviewResponse> getAllReviews() {
        List<Reviews> list = reviewsRepository.findAll();
        if (list == null || list.isEmpty()) return List.of();
        // Ensure stable sorting by id DESC
        List<Reviews> sorted = sorter.sort(list,
                Comparator.comparing(Reviews::getId, Comparator.nullsLast(Long::compareTo)).reversed());
        return sorted.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public ReviewResponse createReview(ReviewRequest request) {
        Product product = productService.getProductById(request.getProductId());
        User user = userService.getUserByIdForReview(request.getUserId());

        Reviews review = new Reviews(request.getRating(), request.getDescription(), user, product);
        Reviews saved = reviewsRepository.save(review);
        return toResponse(saved);

    }

    @Override
    public ReviewResponse getReview(Long id) {
        Reviews review = reviewsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
        return toResponse(review);
    }

    @Override
    public List<ReviewResponse> getReviewsByProduct(Long productId) {
        productService.getProductById(productId); // validate exists
        return reviewsRepository.findByProduct_IdOrderByIdDesc(productId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getReviewsByUser(Long userId) {
        userService.getUserByIdForReview(userId); // validate exists
        return reviewsRepository.findByUser_IdOrderByIdDesc(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public void deleteReview(Long id) {
        if (reviewsRepository.findById(id).isEmpty()) throw new EntityNotFoundException("Review not found");
        reviewsRepository.deleteById(id);
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
