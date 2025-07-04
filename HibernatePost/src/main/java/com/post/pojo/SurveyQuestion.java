/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.pojo;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "surveyquestion")
public class SurveyQuestion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private SurveyPost surveyId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "response_type", nullable = false)
    private ResponseType responseType = ResponseType.SINGLE_CHOICE;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    @UpdateTimestamp
    private LocalDateTime updateAt;
    
    //Caau hoi có thể có nhiều lua chon tra loi SurveyOption
    @OneToMany(mappedBy = "surveyQuestionId", cascade = CascadeType.ALL)
    private Set<SurveyOption> surveyOption;

    // Enum tương ứng với ENUM trong CSDL
    public enum ResponseType {
        SINGLE_CHOICE,
        MULTIPLE_CHOICE,
        TEXT
    }
    
    public SurveyQuestion() {
    }

    public SurveyQuestion(SurveyPost surveyId, String content, ResponseType responseType) {
        this.surveyId = surveyId;
        this.content = content;
        this.responseType = responseType;
    }

    // Getters và Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SurveyPost getSurvey() {
        return surveyId;
    }

    public void setSurvey(SurveyPost survey) {
        this.surveyId = survey;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    @Override
    public String toString() {
        return "SurveyQuestion{" +
                "id=" + id +
                ", surveyId=" + (surveyId != null ? surveyId.getId() : "null") +
                ", content='" + content + '\'' +
                ", responseType=" + responseType +
                ", createdAt=" + createdAt +
                ", updateAt=" + updateAt +
                '}';
    }

    /**
     * @return the surveyOption
     */
    public Set<SurveyOption> getSurveyOption() {
        return surveyOption;
    }

    /**
     * @param surveyOption the surveyOption to set
     */
    public void setSurveyOption(Set<SurveyOption> surveyOption) {
        this.surveyOption = surveyOption;
    }
}

