package com.GameFlix.service;

import com.GameFlix.model.Game;
import com.GameFlix.model.Review;
import com.GameFlix.model.User;
import com.GameFlix.repository.ReviewRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final GameService gameService;

    public ReviewService(ReviewRepository reviewRepository, GameService gameService) {
        this.reviewRepository = reviewRepository;
        this.gameService = gameService;
    }

    public List<Review> forGame(Long gameId) {
        gameService.getGame(gameId);
        return reviewRepository.findByGameGameIdOrderByDateOfCommentDescReviewIdDesc(gameId);

    }

    public Review addReview(User user, Long gameId, String comment) {
        if (comment.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment is required");
        }
        Game game = gameService.getGame(gameId);
        Review r = new Review();
        r.setUser(user);
        r.setGame(game);
        r.setComment(comment.trim());
        r.setDateOfComment(LocalDate.now());
        return reviewRepository.save(r);
    }
}
