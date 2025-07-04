/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.pojo;

import jakarta.persistence.Basic;
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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
import org.hibernate.annotations.CreationTimestamp;

/**
 *
 * @author ASUS
 */
@Entity
@Table(name = "reaction")
//@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Reaction.findAll", query = "SELECT r FROM Reaction r"),
    @NamedQuery(name = "Reaction.findByUserId", query = "SELECT r FROM Reaction r WHERE r.reactionPK.userId = :userId"),
    @NamedQuery(name = "Reaction.findByPostId", query = "SELECT r FROM Reaction r WHERE r.reactionPK.postId = :postId"),
    @NamedQuery(name = "Reaction.findByReactionType", query = "SELECT r FROM Reaction r WHERE r.reactionType = :reactionType"),
    @NamedQuery(name = "Reaction.findByCreatedAt", query = "SELECT r FROM Reaction r WHERE r.createdAt = :createdAt")})
public class Reaction implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ReactionPK reactionPK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "reaction_type")
    private String reactionType;
    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;
    @JoinColumn(name = "post_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Post post;
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private User user;

    public Reaction() {
    }

    public Reaction(ReactionPK reactionPK) {
        this.reactionPK = reactionPK;
    }

    public Reaction(ReactionPK reactionPK, String reactionType) {
        this.reactionPK = reactionPK;
        this.reactionType = reactionType;
    }

    public Reaction(int userId, int postId) {
        this.reactionPK = new ReactionPK(userId, postId);
    }

    public ReactionPK getReactionPK() {
        return reactionPK;
    }

    public void setReactionPK(ReactionPK reactionPK) {
        this.reactionPK = reactionPK;
    }

    public String getReactionType() {
        return reactionType;
    }

    public void setReactionType(String reactionType) {
        this.reactionType = reactionType;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
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
        hash += (reactionPK != null ? reactionPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Reaction)) {
            return false;
        }
        Reaction other = (Reaction) object;
        if ((this.reactionPK == null && other.reactionPK != null) || (this.reactionPK != null && !this.reactionPK.equals(other.reactionPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.post.pojo.Reaction[ reactionPK=" + reactionPK + " ]";
    }
    
}
