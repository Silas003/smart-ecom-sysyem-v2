package com.amalitech.demo.services;

import com.amalitech.demo.dto.request.ReviewRequest;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.models.Reviews;
import com.amalitech.demo.models.User;
import com.amalitech.demo.repository.ReviewsRepository;
import com.amalitech.demo.utils.Sorter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewsServiceTest {

    @Mock
    private ReviewsRepository reviewsRepository;

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @Mock
    private Sorter<Reviews> sorter;

    @InjectMocks
    private ReviewsService reviewsService;

    @Test
    void getAllReviews_sorted_returnsResponses() {
        Reviews a = new Reviews(); a.setId(1L);
        // set product and user to avoid NPE in toResponse
        Product pa = new Product(); pa.setId(10L);
        User ua = new User(); ua.setId(100L); ua.setUsername("userA");
        a.setProduct(pa); a.setUser(ua);

        Reviews b = new Reviews(); b.setId(2L);
        Product pb = new Product(); pb.setId(20L);
        User ub = new User(); ub.setId(200L); ub.setUsername("userB");
        b.setProduct(pb); b.setUser(ub);

        when(reviewsRepository.findAll()).thenReturn(List.of(b,a));
        when(sorter.sort(anyList(), any())).thenReturn(List.of(a,b));
        // lenient stubbings: these are not required by this test's verification but
        // keep them to avoid NPEs if internal calls occur
        org.mockito.Mockito.lenient().when(userService.getUserByIdForReview(anyLong())).thenReturn(new User());
        org.mockito.Mockito.lenient().when(productService.getProductById(anyLong())).thenReturn(new Product());
        // internal toResponse uses model fields; we'll spy the service behavior by calling getAllReviews
        var resp = reviewsService.getAllReviews();
        assertNotNull(resp);
        verify(sorter, times(1)).sort(anyList(), any());
    }

    @Test
    void createReview_success_returnsResponse() {
        ReviewRequest req = new ReviewRequest();
        req.setProductId(1L);
        req.setUserId(2L);
        req.setRating(5);
        req.setDescription("ok");

        User user = new User();
        user.setId(2L);

        Product product = new Product();
        product.setId(1L);

        when(productService.getProductById(1L)).thenReturn(product);
        when(userService.getUserByIdForReview(2L)).thenReturn(user);

        Reviews saved = new Reviews();
        saved.setId(11L);
        saved.setProduct(product);
        saved.setUser(user);

        when(reviewsRepository.save(any())).thenReturn(saved);

        var resp = reviewsService.createReview(req);

        assertNotNull(resp);
        assertEquals(11L, resp.getId());
    }


    @Test
    void getReview_notFound_throws() {
        when(reviewsRepository.findById(9L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> reviewsService.getReview(9L));
    }

    @Test
    void getReviewsByProduct_valid_callsRepository() {
        when(productService.getProductById(5L)).thenReturn(new Product());
        when(reviewsRepository.findByProduct_IdOrderByIdDesc(5L)).thenReturn(List.of());
        var list = reviewsService.getReviewsByProduct(5L);
        assertNotNull(list);
        verify(reviewsRepository, times(1)).findByProduct_IdOrderByIdDesc(5L);
    }

    @Test
    void deleteReview_notFound_throws() {
        when(reviewsRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> reviewsService.deleteReview(99L));
    }
}
