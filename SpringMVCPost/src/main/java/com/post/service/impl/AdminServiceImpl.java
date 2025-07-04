/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.service.impl;

import com.post.pojo.Admin;
import com.post.pojo.Invitationpost;
import com.post.pojo.Surveypost;
import com.post.repositories.AdminRepository;
import com.post.service.AdminService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ASUS
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepo;

    @Override
    public List<Admin> getAdmins(Map<String, String> params) {
        return this.adminRepo.getAdmins(params);
    }

    @Override
    public Admin getAdminById(int id) {
       return this.adminRepo.getAdminById(id);       
    }

    @Override
    public void deleteAdmin(int id) {
        this.adminRepo.deleteAdmin(id);
    }

    @Override
    public List<Surveypost> getSurveyposts(int userId) {
        return this.adminRepo.getSurveyposts(userId);
    }

    @Override
    public List<Invitationpost> getInvitationposts(int userId) {
        return this.adminRepo.getInvitationposts(userId);
    }

}
