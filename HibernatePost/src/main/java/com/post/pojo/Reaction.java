/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.pojo;

/**
 *
 * @author ASUS
 */
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "reaction", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "post_id"})
})
public class Reaction implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @Id
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Posts postId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false)
    private ReactionType reactionType;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    public Reaction() {
    }

    public Reaction(User user, Posts post, ReactionType reactionType) {
        this.userId = user;
        this.postId = post;
        this.reactionType = reactionType;
    }

    public enum ReactionType {
        LIKE, LOVE, HAHA, WOW, SAD, ANGRY
    }

    public User getUser() {
        return userId;
    }

    public void setUser(User user) {
        this.userId = user;
    }

    public Posts getPost() {
        return postId;
    }

    public void setPost(Posts post) {
        this.postId = post;
    }

    public ReactionType getReactionType() {
        return reactionType;
    }

    public void setReactionType(ReactionType reactionType) {
        this.reactionType = reactionType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
