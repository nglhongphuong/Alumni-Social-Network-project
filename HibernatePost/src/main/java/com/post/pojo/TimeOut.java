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
import org.hibernate.annotations.CreationTimestamp;
import java.io.Serializable;

@Entity
@Table(name = "timeout")
public class TimeOut implements Serializable {

    @Id
    @Column(name = "lecturer_id")
    private Integer id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "lecturer_id")
    private Lecturer lecturer;

    @Column(name = "expired_at") //Thời gian kết thúc
    private LocalDateTime expiredAt;

    public TimeOut() {
        LocalDateTime expiredTime = LocalDateTime.now().plusHours(24);
        this.expiredAt = expiredTime;
    }

    public TimeOut(Lecturer lecturer) {
        this.lecturer = lecturer;
        this.id = lecturer != null ? lecturer.getUserId() : null;
        LocalDateTime expiredTime = LocalDateTime.now().plusHours(24);
        this.expiredAt = expiredTime;
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
     * @return the lecturer
     */
    public Lecturer getLecturer() {
        return lecturer;
    }

    /**
     * @param lecturer the lecturer to set
     */
    public void setLecturer(Lecturer lecturer) {
        this.lecturer = lecturer;
    }

    /**
     * @return the expiredAt
     */
    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    /**
     * @param expiredAt the expiredAt to set
     */
    public void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

}
