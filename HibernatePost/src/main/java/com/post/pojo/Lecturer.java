/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.pojo;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "lecturer")
public class Lecturer implements Serializable {

    @Id
    @Column(name = "user_id")
    private int userId;

    @OneToOne
    @MapsId //map id tu lop user lam khoa chinh -> ko tach biet
    @JoinColumn(name = "user_id")
    private User user;

    //1..0 đến Timeout optional = True -> có hoặc không tùy chọn
    @OneToOne(mappedBy = "lecturer", optional = true)
    private TimeOut timeOut;

    // Constructor mặc định
    public Lecturer() {
    }

    // Constructor với User
    public Lecturer(User user) {
        this.user = user;
        if (user != null) {
            this.userId = user.getId(); // Giả sử User có phương thức getId()
        }
        this.timeOut = new TimeOut();
        this.timeOut.setLecturer(this);
//        // Tính toán và in ra thời gian đã cộng thêm 24 giờ
//        LocalDateTime expiredTime = LocalDateTime.now().plusHours(24);
//        System.out.println("Expired Time: " + expiredTime);  // In thời gian tính được
//        this.timeOut.setExpiredAt(expiredTime);

    }

    // Constructor với User và TimeOut
    public Lecturer(User user, TimeOut timeOut) {
        this.user = user;
        if (user != null) {
            this.userId = user.getId();
        }
        this.timeOut = timeOut;
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

    /**
     * @return the timeOut
     */
    public TimeOut getTimeOut() {
        return timeOut;
    }

    /**
     * @param timeOut the timeOut to set
     */
    public void setTimeOut(TimeOut timeOut) {
        this.timeOut = timeOut;
    }

    @Override
    public String toString() {
        return "Lecturer{"
                + "userId=" + userId
                + ", name=" + (user != null ? user.getName() : "null")
                + ", username=" + (user != null ? user.getUsername() : "null")
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Lecturer)) {
            return false;
        }
        Lecturer other = (Lecturer) o;
        return userId == other.userId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(userId);
    }

}
