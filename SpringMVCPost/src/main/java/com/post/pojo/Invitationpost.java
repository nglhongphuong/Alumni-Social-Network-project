/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
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
@Table(name = "invitationpost")
@NamedQueries({
    @NamedQuery(name = "Invitationpost.findAll", query = "SELECT i FROM Invitationpost i"),
    @NamedQuery(name = "Invitationpost.findById", query = "SELECT i FROM Invitationpost i WHERE i.id = :id"),
    @NamedQuery(name = "Invitationpost.findByTitle", query = "SELECT i FROM Invitationpost i WHERE i.title = :title"),
    @NamedQuery(name = "Invitationpost.findByRecipientScope", query = "SELECT i FROM Invitationpost i WHERE i.recipientScope = :recipientScope"),
    @NamedQuery(name = "Invitationpost.findByCreatedAt", query = "SELECT i FROM Invitationpost i WHERE i.createdAt = :createdAt"),
    @NamedQuery(name = "Invitationpost.findByUpdateAt", query = "SELECT i FROM Invitationpost i WHERE i.updateAt = :updateAt")})
public class Invitationpost implements Serializable {

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
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "content")
    private String content;
    @Size(max = 12)
    @Column(name = "recipient_scope")
    private String recipientScope;
    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;
    @Column(name = "update_at")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updateAt;
    @ManyToMany(mappedBy = "invitationpostSet")
    @JsonIgnore
    private Set<User> userSet;
    @JoinColumn(name = "admin_id", referencedColumnName = "user_id")
    @JsonIgnore
    @ManyToOne(optional = false)
    private Admin adminId;

    public Invitationpost() {
    }

    public Invitationpost(Integer id) {
        this.id = id;
    }

    public Invitationpost(Integer id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRecipientScope() {
        return recipientScope;
    }

    public void setRecipientScope(String recipientScope) {
        this.recipientScope = recipientScope;
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

    public Set<User> getUserSet() {
        return userSet;
    }

    public void setUserSet(Set<User> userSet) {
        this.userSet = userSet;
    }

    public Admin getAdminId() {
        return adminId;
    }

    public void setAdminId(Admin adminId) {
        this.adminId = adminId;
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
        if (!(object instanceof Invitationpost)) {
            return false;
        }
        Invitationpost other = (Invitationpost) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.post.pojo.Invitationpost[ id=" + id + " ]";
    }

}
