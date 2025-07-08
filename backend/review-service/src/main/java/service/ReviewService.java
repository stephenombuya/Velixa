package com.velixa.reviews.service;

import com.velixa.reviews.dto.ReviewDTO;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    ReviewDTO addReview(ReviewDTO reviewDTO);
    Optional<ReviewDTO> getReviewById(String id);
    List<ReviewDTO> getReviewsByProduct(String productId);
    void deleteReview(String id);
}
