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

@Entity
@Table(name = "alumni")

public class Alumni implements Serializable {

    @Id
    @Column(name = "user_id")
    private int userId;

    @Column(name = "student_code", unique = true, nullable = false)
    private String studentCode;

    @OneToOne
    @MapsId //map id tu lop user lam khoa chinh -> ko tach biet
    @JoinColumn(name = "user_id")
    private User user;
    
    public Alumni(){
        
    }
    public Alumni(int userId, String studentCode, User user) {
        this.userId = userId;
        this.studentCode = studentCode;
        this.user = user;
    }
    public Alumni(String studentCode, User user) {
        this.studentCode = studentCode;
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
     * @return the studentCode
     */
    public String getStudentCode() {
        return studentCode;
    }

    /**
     * @param studentCode the studentCode to set
     */
    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
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
        return "Alumni{"
                + "userId=" + userId
                + ", studentCode='" + studentCode + '\''
                + ", name=" + (user != null ? user.getName() : "null")
                + ", username=" + (user != null ? user.getUsername() : "null")
                + '}';
    }

}
