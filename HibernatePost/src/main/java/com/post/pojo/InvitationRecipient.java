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
import java.io.Serializable;

@Entity
@Table(name = "invitationrecipient", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "invitation_post_id"})
})
public class InvitationRecipient implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @Id
    @ManyToOne
    @JoinColumn(name = "invitation_post_id", nullable = false)
    private InvitationPost invitationPostId;

    public InvitationRecipient() {
    }

    public InvitationRecipient(User userId, InvitationPost invitationPostId) {
        this.userId = userId;
        this.invitationPostId = invitationPostId;
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
    public InvitationPost getInvitationPostId() {
        return invitationPostId;
    }

    /**
     * @param invitationPostId the invitationPostId to set
     */
    public void setInvitationPostId(InvitationPost invitationPostId) {
        this.invitationPostId = invitationPostId;
    }

    @Override
    public String toString() {
        return "InvitationRecipient{"
                + "userId=" + (userId != null ? userId.getId() : null)
                + ", invitationPostId=" + (invitationPostId != null ? invitationPostId.getId() : null)
                + '}';
    }

}
