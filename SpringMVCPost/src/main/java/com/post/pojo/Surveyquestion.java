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
@Table(name = "surveyquestion")

@NamedQueries({
    @NamedQuery(name = "Surveyquestion.findAll", query = "SELECT s FROM Surveyquestion s"),
    @NamedQuery(name = "Surveyquestion.findById", query = "SELECT s FROM Surveyquestion s WHERE s.id = :id"),
    @NamedQuery(name = "Surveyquestion.findByResponseType", query = "SELECT s FROM Surveyquestion s WHERE s.responseType = :responseType"),
    @NamedQuery(name = "Surveyquestion.findByCreatedAt", query = "SELECT s FROM Surveyquestion s WHERE s.createdAt = :createdAt"),
    @NamedQuery(name = "Surveyquestion.findByUpdateAt", query = "SELECT s FROM Surveyquestion s WHERE s.updateAt = :updateAt")})
public class Surveyquestion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "content")
    private String content;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "response_type")
    private String responseType;
    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;
    @Column(name = "update_at")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updateAt;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionId")
    @JsonIgnore
    private Set<Surveyoption> surveyoptionSet;
    @JoinColumn(name = "survey_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    @JsonIgnore
    private Surveypost surveyId;

    public Surveyquestion() {
    }

    public Surveyquestion(Integer id) {
        this.id = id;
    }

    public Surveyquestion(Integer id, String content, String responseType) {
        this.id = id;
        this.content = content;
        this.responseType = responseType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
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

    public Set<Surveyoption> getSurveyoptionSet() {
        return surveyoptionSet;
    }

    public void setSurveyoptionSet(Set<Surveyoption> surveyoptionSet) {
        this.surveyoptionSet = surveyoptionSet;
    }

    public Surveypost getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Surveypost surveyId) {
        this.surveyId = surveyId;
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
        if (!(object instanceof Surveyquestion)) {
            return false;
        }
        Surveyquestion other = (Surveyquestion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.post.pojo.Surveyquestion[ id=" + id + " ]";
    }

}
