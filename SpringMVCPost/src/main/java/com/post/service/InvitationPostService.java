/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.post.service;

import com.post.pojo.Invitationpost;
import java.util.List;
import java.util.Map;
/**
 *
 * @author ASUS
 */
public interface InvitationPostService {
     List<Invitationpost> getInvitationPosts(Map<String, String> params);

    Invitationpost getInvitationPostById(int id);

    Invitationpost createOrUpdate(Invitationpost sp);

    void deleteInvitationPost(int id);

    Map<String, Object> getInvitationRecipients(int invitationPostId, Map<String, String> params);
}
