package com.GameFlix.model;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    @Column(length = 255)
    private String title;

    @Column(length = 255)
    private String genre;

    @Column(length = 2000)
    private String description;

    private LocalDate releaseDate;

    @Column(nullable = false)
    private boolean kidFriendly;

    // getters
    public Long getGameId() {
        return gameId;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public boolean isKidFriendly() {
        return kidFriendly;
    }

    // setters
    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setKidFriendly(boolean kidFriendly) {
        this.kidFriendly = kidFriendly;
    }
}