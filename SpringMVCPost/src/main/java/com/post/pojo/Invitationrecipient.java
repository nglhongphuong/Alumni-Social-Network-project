/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @author ASUS
 */
@Entity
@Table(name = "invitationrecipient", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "invitation_post_id"})
})
public class Invitationrecipient implements Serializable {
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "user_id")
    private User userId;

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "invitation_post_id", nullable = false)
    private Invitationpost invitationPostId;

    public Invitationrecipient() {

    }

    public Invitationrecipient(User u, Invitationpost i) {
        this.userId = u;
        this.invitationPostId = i;
    }

    /**
     * @return the userId
     */
    public User getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(User userId) {
        this.userId = userId;
    }

    /**
     * @return the invitationPostId
     */
    public Invitationpost getInvitationPostId() {
        return invitationPostId;
    }

    /**
     * @param invitationPostId the invitationPostId to set
     */
    public void setInvitationPostId(Invitationpost invitationPostId) {
        this.invitationPostId = invitationPostId;
    }

}
