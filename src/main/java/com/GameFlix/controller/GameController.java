package com.GameFlix.controller;

import com.GameFlix.model.Game;
import com.GameFlix.model.Review;
import com.GameFlix.model.User;
import com.GameFlix.service.CurrentUser;
import com.GameFlix.service.GameService;
import com.GameFlix.service.ReviewService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
public class GameController {
    private final GameService gameService;
    private final ReviewService reviewService;
    private final CurrentUser currentUser;

    public GameController(GameService gameService, ReviewService reviewService, CurrentUser currentUser) {
        this.gameService = gameService;
        this.reviewService = reviewService;
        this.currentUser = currentUser;
    }

    // GET the list of games
    @GetMapping
    public List<Game> browse() {
        return gameService.browse();
    }

    // GET /{id}
    @GetMapping("/{id}")
    public Game gameDetails(@PathVariable Long id) {
        return gameService.getGame(id);
    }

    // POST /{id}/play
    @PostMapping("/{id}/play")
    public Map<String, Object> playGame(Authentication auth, @PathVariable Long id) {
        Game game = gameService.play(currentUser.from(auth), id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "Now playing " + game.getTitle());
        result.put("gameId", game.getGameId());
        result.put("title", game.getTitle());
        return result;
    }

    // GET /{id}/reviews
    @GetMapping("/{id}/reviews")
    public List<Map<String, Object>> reviews(@PathVariable Long id) {
        return reviewService.forGame(id).stream().map(this::toMap).toList();
    }

    // POST /{id}/reviews
    @PostMapping("/{id}/reviews")
    public ResponseEntity<Map<String, Object>> addReview(Authentication auth, @PathVariable Long id, @RequestBody Map<String, String> body) {
        User user = currentUser.from(auth);
        Review r = reviewService.addReview(user, id, body.get("comment"));
        return ResponseEntity.status(HttpStatus.CREATED).body(toMap(r));
    }

    // never return the User entity directly (it has the password hash)
    private Map<String, Object> toMap(Review r) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("reviewId", r.getReviewId());
        m.put("gameId", r.getGame().getGameId());
        m.put("reviewer", r.getUser().getUsername() != null ? r.getUser().getUsername() : r.getUser().getFirstName());
        m.put("comment", r.getComment());
        m.put("dateOfComment", r.getDateOfComment());
        return m;
    }
}