package com.GameFlix.repository;

import com.GameFlix.model.Game;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByKidFriendlyTrue();
}
