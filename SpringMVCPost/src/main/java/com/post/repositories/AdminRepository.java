/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.post.repositories;

import com.post.pojo.Admin;
import com.post.pojo.Invitationpost;
import com.post.pojo.Surveypost;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ASUS
 */
public interface AdminRepository {

    List<Admin> getAdmins(Map<String, String> params);

    Admin getAdminById(int id);

    void deleteAdmin(int id);

    List<Surveypost> getSurveyposts(int userId);

    List<Invitationpost> getInvitationposts(int userId);
}
