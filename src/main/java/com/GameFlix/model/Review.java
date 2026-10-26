package com.GameFlix.model;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "game_id")
    private Game game;

    @Column(length = 2000)
    private String comment;

    private LocalDate dateOfComment;

    // getters
    public Long getReviewId() {
        return reviewId;
    }

    public User getUser() {
        return user;
    }

    public Game getGame() {
        return game;
    }

    public String getComment() {
        return comment;
    }

    public LocalDate getDateOfComment() {
        return dateOfComment;
    }

    // setters
    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setDateOfComment(LocalDate dateOfComment) {
        this.dateOfComment = dateOfComment;
    }
}