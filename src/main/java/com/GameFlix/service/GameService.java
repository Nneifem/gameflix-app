package com.GameFlix.service;

import com.GameFlix.model.Game;
import com.GameFlix.model.User;
import com.GameFlix.repository.GameRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final SubscriptionService subscriptionService;

    public GameService(GameRepository gameRepository, SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
        this.gameRepository = gameRepository;
    }

    public List<Game> browse() {
        return gameRepository.findAll();
    }

    public Game getGame(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));
    }

    public Game play(User user, Long gameId) {
        if (!subscriptionService.hasActive(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "An active subscription is required to play");
        }
        return getGame(gameId);
    }
}
