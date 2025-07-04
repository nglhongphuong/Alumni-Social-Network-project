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
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "post")
public class Posts implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "is_comment_locked")
    private Boolean isCommentLocked = false;

    private String image;

    @Enumerated(EnumType.STRING)
    private Visibility visibility = Visibility.PUBLIC;

    //Xóa/Chỉnh sửa post thì comment thuộc post đó cũng chỉnh sửa theo
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "postId")
    private Set<Comment> comment;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "postId")
    private Set<Reaction> reaction;

    // Chế độ cho bài viết
    public enum Visibility {
        PRIVATE,
        PUBLIC
    }
    public Posts() {
        // Hibernate cần constructor mặc định
    }

    public Posts(User user, String content, Boolean isCommentLocked, String image,
                 Visibility visibility, Set<Comment> comment, Set<Reaction> reaction) {
        this.user = user;
        this.content = content;
        this.isCommentLocked = isCommentLocked;
        this.image = image;
        this.visibility = visibility;
        this.comment = comment != null ? comment : new HashSet<>(); // đảm bảo comment không bị null
        this.reaction = reaction != null ? reaction : new HashSet<>();
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the createdAt
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return the updatedAt
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt the updatedAt to set
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @return the isCommentLocked
     */
    public Boolean getIsCommentLocked() {
        return isCommentLocked;
    }

    /**
     * @param isCommentLocked the isCommentLocked to set
     */
    public void setIsCommentLocked(Boolean isCommentLocked) {
        this.isCommentLocked = isCommentLocked;
    }

    /**
     * @return the image
     */
    public String getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * @return the visibility
     */
    public Visibility getVisibility() {
        return visibility;
    }

    /**
     * @param visibility the visibility to set
     */
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    /**
     * @return the comment
     */
    public Set<Comment> getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(Set<Comment> comment) {
        this.comment = comment;
    }

    /**
     * @return the reaction
     */
    public Set<Reaction> getReaction() {
        return reaction;
    }

    /**
     * @param reaction the reaction to set
     */
    public void setReaction(Set<Reaction> reaction) {
        this.reaction = reaction;
    }

    @Override
    public String toString() {
        return "Posts{"
                + "id=" + id
                + ", user=" + (user != null ? user.getId() : "null")
                + ", content='" + content + '\''
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + ", isCommentLocked=" + isCommentLocked
                + ", image='" + image + '\''
                + ", visibility=" + visibility
                + ", commentCount=" + (comment != null ? comment.size() : 0)
                + ", reactionCount=" + (reaction != null ? reaction.size() : 0)
                + '}';
    }

}
