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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
@Table(name = "surveyoption")

@NamedQueries({
    @NamedQuery(name = "Surveyoption.findAll", query = "SELECT s FROM Surveyoption s"),
    @NamedQuery(name = "Surveyoption.findById", query = "SELECT s FROM Surveyoption s WHERE s.id = :id"),
    @NamedQuery(name = "Surveyoption.findByContent", query = "SELECT s FROM Surveyoption s WHERE s.content = :content"),
    @NamedQuery(name = "Surveyoption.findByCreatedAt", query = "SELECT s FROM Surveyoption s WHERE s.createdAt = :createdAt"),
    @NamedQuery(name = "Surveyoption.findByUpdateAt", query = "SELECT s FROM Surveyoption s WHERE s.updateAt = :updateAt")})
public class Surveyoption implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 555)
    @Column(name = "content")
    private String content;
    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;
    @Column(name = "update_at")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updateAt;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "surveyoption")
    @JsonIgnore
    private Set<Responseoption> responseoptionSet;
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Surveyquestion questionId;

    public Surveyoption() {
    }

    public Surveyoption(Integer id) {
        this.id = id;
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


    public Set<Responseoption> getResponseoptionSet() {
        return responseoptionSet;
    }

    public void setResponseoptionSet(Set<Responseoption> responseoptionSet) {
        this.responseoptionSet = responseoptionSet;
    }

    public Surveyquestion getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Surveyquestion questionId) {
        this.questionId = questionId;
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
        if (!(object instanceof Surveyoption)) {
            return false;
        }
        Surveyoption other = (Surveyoption) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.post.pojo.Surveyoption[ id=" + id + " ]";
    }

}
