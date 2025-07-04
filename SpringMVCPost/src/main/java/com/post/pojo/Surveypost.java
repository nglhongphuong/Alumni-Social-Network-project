/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author ASUS
 */
@Entity
@Table(name = "surveypost")

@NamedQueries({
    @NamedQuery(name = "Surveypost.findAll", query = "SELECT s FROM Surveypost s"),
    @NamedQuery(name = "Surveypost.findById", query = "SELECT s FROM Surveypost s WHERE s.id = :id"),
    @NamedQuery(name = "Surveypost.findByTitle", query = "SELECT s FROM Surveypost s WHERE s.title = :title"),
    @NamedQuery(name = "Surveypost.findByStartDate", query = "SELECT s FROM Surveypost s WHERE s.startDate = :startDate"),
    @NamedQuery(name = "Surveypost.findByEndDate", query = "SELECT s FROM Surveypost s WHERE s.endDate = :endDate"),
    @NamedQuery(name = "Surveypost.findByCreatedAt", query = "SELECT s FROM Surveypost s WHERE s.createdAt = :createdAt"),
    @NamedQuery(name = "Surveypost.findByUpdateAt", query = "SELECT s FROM Surveypost s WHERE s.updateAt = :updateAt"),
    @NamedQuery(name = "Surveypost.findByStatus", query = "SELECT s FROM Surveypost s WHERE s.status = :status")})
public class Surveypost implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "title")
    private String title;
    @Lob
    @Size(max = 65535)
    @Column(name = "description")
    private String description;
    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;
    @Column(name = "update_at")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updateAt;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 7)
    @Column(name = "status")
    private String status;
    @JoinColumn(name = "admin_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    @JsonIgnore
    private Admin adminId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "surveyId")
    @JsonIgnore
    private Set<Surveyquestion> surveyquestionSet;

    public Surveypost() {
    }

    public Surveypost(Integer id) {
        this.id = id;
    }

    public Surveypost(Integer id, String title, String status) {
        this.id = id;
        this.title = title;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Admin getAdminId() {
        return adminId;
    }

    public void setAdminId(Admin adminId) {
        this.adminId = adminId;
    }

    public Set<Surveyquestion> getSurveyquestionSet() {
        return surveyquestionSet;
    }

    public void setSurveyquestionSet(Set<Surveyquestion> surveyquestionSet) {
        this.surveyquestionSet = surveyquestionSet;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Surveypost)) {
            return false;
        }
        Surveypost other = (Surveypost) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.post.pojo.Surveypost[ id=" + id + " ]";
    }

}
