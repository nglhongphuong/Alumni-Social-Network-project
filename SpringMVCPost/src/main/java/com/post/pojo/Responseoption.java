/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
import org.hibernate.annotations.CreationTimestamp;

/**
 *
 * @author ASUS
 */
@Entity
@Table(name = "responseoption")

@NamedQueries({
    @NamedQuery(name = "Responseoption.findAll", query = "SELECT r FROM Responseoption r"),
    @NamedQuery(name = "Responseoption.findByUserId", query = "SELECT r FROM Responseoption r WHERE r.responseoptionPK.userId = :userId"),
    @NamedQuery(name = "Responseoption.findByOptionId", query = "SELECT r FROM Responseoption r WHERE r.responseoptionPK.optionId = :optionId"),
    @NamedQuery(name = "Responseoption.findByRespondedAt", query = "SELECT r FROM Responseoption r WHERE r.respondedAt = :respondedAt")})
public class Responseoption implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ResponseoptionPK responseoptionPK;
    @Column(name = "responded_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date respondedAt;
    @JoinColumn(name = "option_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    @JsonIgnore
    private Surveyoption surveyoption;
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    @JsonIgnore
    private User user;

    public Responseoption() {
    }

    public Responseoption(ResponseoptionPK responseoptionPK) {
        this.responseoptionPK = responseoptionPK;
    }

    public Responseoption(int userId, int optionId) {
        this.responseoptionPK = new ResponseoptionPK(userId, optionId);
    }

    public ResponseoptionPK getResponseoptionPK() {
        return responseoptionPK;
    }

    public void setResponseoptionPK(ResponseoptionPK responseoptionPK) {
        this.responseoptionPK = responseoptionPK;
    }

    public Date getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(Date respondedAt) {
        this.respondedAt = respondedAt;
    }

    public Surveyoption getSurveyoption() {
        return surveyoption;
    }

    public void setSurveyoption(Surveyoption surveyoption) {
        this.surveyoption = surveyoption;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (responseoptionPK != null ? responseoptionPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Responseoption)) {
            return false;
        }
        Responseoption other = (Responseoption) object;
        if ((this.responseoptionPK == null && other.responseoptionPK != null) || (this.responseoptionPK != null && !this.responseoptionPK.equals(other.responseoptionPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.post.pojo.Responseoption[ responseoptionPK=" + responseoptionPK + " ]";
    }

}
