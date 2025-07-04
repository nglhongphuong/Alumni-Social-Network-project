/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @author ASUS
 */
@Embeddable
public class ResponseoptionPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "user_id")
    private int userId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "option_id")
    private int optionId;

    public ResponseoptionPK() {
    }

    public ResponseoptionPK(int userId, int optionId) {
        this.userId = userId;
        this.optionId = optionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getOptionId() {
        return optionId;
    }

    public void setOptionId(int optionId) {
        this.optionId = optionId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) userId;
        hash += (int) optionId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ResponseoptionPK)) {
            return false;
        }
        ResponseoptionPK other = (ResponseoptionPK) object;
        if (this.userId != other.userId) {
            return false;
        }
        if (this.optionId != other.optionId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.post.pojo.ResponseoptionPK[ userId=" + userId + ", optionId=" + optionId + " ]";
    }
    
}
