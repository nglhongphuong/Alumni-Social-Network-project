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
import java.util.Set;

@Entity
@Table(name = "admin")
public class Admin implements Serializable {

    @Id
    @Column(name = "user_id")
    private int userId;

    @OneToOne
    @MapsId //map id tu lop user lam khoa chinh -> ko tach biet
    @JoinColumn(name = "user_id")
    private User user;

    //Admin có thể có nhiều bài SurveyPost
    @OneToMany(mappedBy = "adminId", cascade = CascadeType.ALL)
    private Set<SurveyPost> surveyPost;
    
    //Admin có thể có nhiều bài InvitationPost
    @OneToMany(mappedBy = "adminId", cascade = CascadeType.ALL)
    private Set<InvitationPost> invitationPost;

    public Admin() {

    }

    public Admin(int userId, User user) {
        this.userId = userId;
        this.user = user;
    }

    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
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

    @Override
    public String toString() {
        return "Admin{"
                + "id=" + this.userId
                + ", name=" + (user != null ? user.getName() : "null")
                + ", username=" + (user != null ? user.getUsername() : "null")
                + "}";
    }

    /**
     * @return the surveyPost
     */
    public Set<SurveyPost> getSurveyPost() {
        return surveyPost;
    }

    /**
     * @param surveyPost the surveyPost to set
     */
    public void setSurveyPost(Set<SurveyPost> surveyPost) {
        this.surveyPost = surveyPost;
    }

    /**
     * @return the invitationPost
     */
    public Set<InvitationPost> getInvitationPost() {
        return invitationPost;
    }

    /**
     * @param invitationPost the invitationPost to set
     */
    public void setInvitationPost(Set<InvitationPost> invitationPost) {
        this.invitationPost = invitationPost;
    }
    
}
