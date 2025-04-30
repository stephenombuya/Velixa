package com.velixa.reviews.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.velixa.reviews.dto.ReviewDTO;
import com.velixa.reviews.model.Review;
import com.velixa.reviews.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

	@Autowired
    private ReviewRepository reviewRepository;

    @Override
    public ReviewDTO addReview(ReviewDTO reviewDTO) {
        Review review = new Review();
        review.setProductId(reviewDTO.getProductId());
        review.setComment(reviewDTO.getComment());
        review.setRating(reviewDTO.getRating());
        review.setCreatedAt(LocalDateTime.now());
              
        review = reviewRepository.save(review);
        
        reviewDTO.setCreatedAt(review.getCreatedAt());
        return reviewDTO;
    }

    @Override
    public Optional<ReviewDTO> getReviewById(String id) {
        return reviewRepository.findById(id)
                .map(this::mapToDTO);
    }

    @Override
    public List<ReviewDTO> getReviewsByProduct(String productId) {
        return reviewRepository.findByProductId(productId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteReview(String id) {
        reviewRepository.deleteById(id);
    }

    private ReviewDTO mapToDTO(Review review) {
    	ReviewDTO reviewDTO = new ReviewDTO();
    	
    	reviewDTO.setProductId(review.getProductId());
    	reviewDTO.setComment(review.getComment());
    	reviewDTO.setRating(review.getRating());
    	reviewDTO.setCreatedAt(review.getCreatedAt());
        
    	return reviewDTO;
    }
}
