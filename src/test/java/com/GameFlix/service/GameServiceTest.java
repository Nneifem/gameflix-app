package com.GameFlix.service;

import com.GameFlix.model.Game;
import com.GameFlix.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GameServiceTest {

    @Autowired
    private GameService gameService;

    // testing to see if the catalog returns full or empty
    @Test
    void testBrowseGameCatalog() {
        List<Game> games = gameService.browse();

        assertNotNull(games);
        assertFalse(games.isEmpty());
    }

    // testing a user with no subscription trying to play
    @Test
    void testUserHasSubscription() {
        User user = new User();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> gameService.play(user, 1L));
        assertEquals(403, exception.getStatusCode().value());
    }

    // testing to find an existing game
    @Test
    void testFindGame() {
        Game game = gameService.getGame(1L);

        assertNotNull(game);
        assertEquals(game.getGameId(), 1L);
    }
}