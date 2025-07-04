/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.pojo;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "surveyoption")
public class SurveyOption implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false) // <-- Đổi đúng tên cột
    private SurveyQuestion surveyQuestionId;

    @Column(length = 555)
    private String content;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    //Cau tra loi có thể có nhiều lua chon tra loi SurveyOption
    @OneToMany(mappedBy = "surveyOptionId", cascade = CascadeType.ALL)
    private Set<ResponseOption> responseOption;
    
    // Constructor mặc định
    public SurveyOption() {
        // Hibernate cần constructor mặc định
    }

    // Constructor có tham số (không bao gồm id, createdAt, updatedAt)
    public SurveyOption(SurveyQuestion surveyQuestionId, String content, Set<ResponseOption> responseOption) {
        this.surveyQuestionId = surveyQuestionId;
        this.content = content;
        this.responseOption = responseOption != null ? responseOption : new HashSet<>();
    }

    // Getter và Setter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SurveyQuestion getQuestion() {
        return surveyQuestionId;
    }

    public void setQuestion(SurveyQuestion question) {
        this.surveyQuestionId = question;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "SurveyOption{"
                + "id=" + id
                + ", content='" + content + '\''
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + '}';
    }

    /**
     * @return the responseOption
     */
    public Set<ResponseOption> getResponseOption() {
        return responseOption;
    }

    /**
     * @param responseOption the responseOption to set
     */
    public void setResponseOption(Set<ResponseOption> responseOption) {
        this.responseOption = responseOption;
    }
}
