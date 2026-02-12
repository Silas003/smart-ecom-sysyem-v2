package com.amalitech.demo.controllers;

import com.amalitech.demo.dto.request.ReviewRequest;
import com.amalitech.demo.dto.response.ReviewResponse;
import com.amalitech.demo.restcontroller.ReviewsController;
import com.amalitech.demo.security.JwtAuthenticationFilter;
import com.amalitech.demo.security.JwtService;
import com.amalitech.demo.services.ReviewsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewsController.class)
@Import({
        JwtAuthenticationFilter.class, JwtService.class})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class ReviewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewsService reviewsService;

    @Test
    void shouldReturnReviews() throws Exception {
        ReviewResponse r = new ReviewResponse();
        r.setId(1L); r.setRating(5); r.setDescription("ok"); r.setProductId(1L); r.setReviewerDisplay("u");
        when(reviewsService.getAllReviews()).thenReturn(List.of(r));

        mockMvc.perform(get("/api/v1/reviews/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("reviews retrieved"));
    }

    @Test
    void shouldCreateReview() throws Exception {
        String req = "{\"productId\":1,\"rating\":5,\"description\":\"ok\"}";
        ReviewResponse r = new ReviewResponse(); r.setId(2L); r.setRating(5); r.setDescription("ok"); r.setProductId(1L); r.setReviewerDisplay("u");
        when(reviewsService.createReview(any(ReviewRequest.class))).thenReturn(r);

        mockMvc.perform(post("/api/v1/reviews/").contentType("application/json").header("X-User-Id", "2").content(req))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("review created"));
    }
}
