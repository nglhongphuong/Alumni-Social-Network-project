/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.service.impl;

import com.post.pojo.Responseoption;
import com.post.pojo.ResponseoptionPK;
import com.post.pojo.Surveyoption;
import com.post.pojo.User;
import com.post.repositories.ResponseOptionRepository;
import com.post.service.ResponseOptionService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ASUS
 */
@Service
public class ResponseOptionServiceImpl implements ResponseOptionService {

    @Autowired
    private ResponseOptionRepository reponseRepo;

    @Override
    public List<Responseoption> getResponse(Map<String, String> params) {
        return this.reponseRepo.getResponse(params);
    }

    @Override
    public Responseoption createOrUpdate(Responseoption r) {
        return this.reponseRepo.createOrUpdate(r);
    }

    @Override
    public void deleteResponseoption(ResponseoptionPK id) {
        this.reponseRepo.deleteResponseoption(id);
    }

    @Override
    public Responseoption getResponseoptionId(ResponseoptionPK id) {
        return this.reponseRepo.getResponseoptionId(id);
    }

    @Override
    public List<User> getUserResponse(int optionId) {
        return this.reponseRepo.getUserResponse(optionId);
    }

    @Override
    public List<Responseoption> getResponsesByUserAndSurvey(int userId, int surveyId) {
        return this.reponseRepo.getResponsesByUserAndSurvey(userId, surveyId); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<Surveyoption> getOptionsByUserId(int userId) {
        return this.reponseRepo.getOptionsByUserId(userId); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
