/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
//import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

/**
 *
 * @author ASUS
 */
@Entity
@Table(name = "timeout")
//@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Timeout.findAll", query = "SELECT t FROM Timeout t"),
    @NamedQuery(name = "Timeout.findByLecturerId", query = "SELECT t FROM Timeout t WHERE t.lecturerId = :lecturerId"),
    @NamedQuery(name = "Timeout.findByExpiredAt", query = "SELECT t FROM Timeout t WHERE t.expiredAt = :expiredAt")})
public class Timeout implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "lecturer_id")
    private Integer lecturerId;
    @Column(name = "expired_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiredAt;
    @JoinColumn(name = "lecturer_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Lecturer lecturer;

    public Timeout() {
    }

    public Timeout(Integer lecturerId) {
        this.lecturerId = lecturerId;
        this.expiredAt =  Timestamp.valueOf(LocalDateTime.now().plusHours(24));
    }

    public Integer getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(Integer lecturerId) {
        this.lecturerId = lecturerId;
    }

    public Date getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Date expiredAt) {
        this.expiredAt = expiredAt;
    }

    public Lecturer getLecturer() {
        return lecturer;
    }

    public void setLecturer(Lecturer lecturer) {
        this.lecturer = lecturer;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (lecturerId != null ? lecturerId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Timeout)) {
            return false;
        }
        Timeout other = (Timeout) object;
        if ((this.lecturerId == null && other.lecturerId != null) || (this.lecturerId != null && !this.lecturerId.equals(other.lecturerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.post.pojo.Timeout[ lecturerId=" + lecturerId + " ]";
    }
    
}
