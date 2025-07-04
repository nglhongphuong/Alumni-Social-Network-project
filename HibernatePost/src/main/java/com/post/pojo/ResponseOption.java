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
@Table(name = "responseoption", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "option_id"})
})
public class ResponseOption implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @Id
    @ManyToOne
    @JoinColumn(name = "option_id", nullable = false)
    private SurveyOption surveyOptionId; 

    @Column(name = "responded_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime respondedAt;
    public ResponseOption() {
    }


    public ResponseOption(User userId, SurveyOption surveyOptionId) {
        this.userId = userId;
        this.surveyOptionId = surveyOptionId;
    }

    /**
     * @return the userId
     */
    public User getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(User userId) {
        this.userId = userId;
    }
    /**
     * @return the respondedAt
     */
    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    /**
     * @param respondedAt the respondedAt to set
     */
    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }

    @Override
    public String toString() {
        return "ResponseOption{"
                + "userId=" + (userId != null ? userId.getId() : null)
                + ", optionId=" + (getSurveyOptionId() != null ? getSurveyOptionId().getId() : null)
                + ", respondedAt=" + respondedAt
                + '}';
    }

    /**
     * @return the surveyOptionId
     */
    public SurveyOption getSurveyOptionId() {
        return surveyOptionId;
    }

    /**
     * @param surveyOptionId the surveyOptionId to set
     */
    public void setSurveyOptionId(SurveyOption surveyOptionId) {
        this.surveyOptionId = surveyOptionId;
    }

}
