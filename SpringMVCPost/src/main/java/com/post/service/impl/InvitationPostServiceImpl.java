/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.service.impl;

import com.post.pojo.Invitationpost;
import com.post.repositories.InvitationPostRepository;
import com.post.service.InvitationPostService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ASUS
 */
@Service
public class InvitationPostServiceImpl implements InvitationPostService{
        @Autowired
        private InvitationPostRepository invitationRepo;

    @Override
    public List<Invitationpost> getInvitationPosts(Map<String, String> params) {
        return this.invitationRepo.getInvitationPosts(params);
    }
    @Override
    public Invitationpost getInvitationPostById(int id) {
        return this.invitationRepo.getInvitationPostById(id);
    }
    @Override
    public Invitationpost createOrUpdate(Invitationpost sp) {
        return this.invitationRepo.createOrUpdate(sp);
    }

    @Override
    public void deleteInvitationPost(int id) {
        this.invitationRepo.deleteInvitationPost(id);
    }

    @Override
    public Map<String, Object> getInvitationRecipients(int invitationPostId, Map<String, String> params) {
        return this.invitationRepo.getInvitationRecipients(invitationPostId, params);
    }
}
