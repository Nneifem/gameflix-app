package com.GameFlix.repository;

import com.GameFlix.model.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByGameGameIdOrderByDateOfCommentDescReviewIdDesc(Long gameId);
}