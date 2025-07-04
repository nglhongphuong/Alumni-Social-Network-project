/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.pojo;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "invitationpost")
public class InvitationPost implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Mối quan hệ N-1 với Admin
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin adminId;

    //Thư mời được nhiều người tham gia đăng ký
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "invitationPostId")
    private Set<InvitationRecipient> invitationPost;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "recipient_scope", nullable = false)
    private RecipientScope recipientScope = RecipientScope.ALL;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updateAt;

    // Phạm vi được mời 
    public enum RecipientScope {
        INDIVIDUAL,//Cá nhân
        ROLE_GROUP,//Nhóm vai trò
        CUSTOM_GROUP, //Tùy chọn
        ALL //Tất cả
    }

    public InvitationPost() {
        // Constructor mặc định
    }

    public InvitationPost(Admin adminId, String title, String content, RecipientScope recipientScope,
            LocalDateTime createdAt, LocalDateTime updateAt, Set<InvitationRecipient> invitationPost) {
        this.adminId = adminId;
        this.title = title;
        this.content = content;
        this.recipientScope = recipientScope;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
        this.invitationPost = invitationPost;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Admin getAdmin() {
        return adminId;
    }

    public void setAdmin(Admin admin) {
        this.adminId = admin;
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

    public RecipientScope getRecipientScope() {
        return recipientScope;
    }

    public void setRecipientScope(RecipientScope recipientScope) {
        this.recipientScope = recipientScope;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    @Override
    public String toString() {
        return "InvitationPost{"
                + "id=" + id
                + ", admin=" + (adminId != null ? adminId.getUserId() : null)
                + ", title='" + title + '\''
                + ", content='" + content + '\''
                + ", recipientScope=" + getRecipientScope()
                + ", createdAt=" + createdAt
                + ", updateAt=" + updateAt
                + '}';
    }

    /**
     * @return the invitationPost
     */
    public Set<InvitationRecipient> getInvitationPost() {
        return invitationPost;
    }

    /**
     * @param invitationPost the invitationPost to set
     */
    public void setInvitationPost(Set<InvitationRecipient> invitationPost) {
        this.invitationPost = invitationPost;
    }
}
