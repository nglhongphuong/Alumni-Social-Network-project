/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.service.impl;

import com.post.pojo.Surveypost;
import com.post.pojo.Surveyquestion;
import com.post.repositories.SurveyPostRepository;
import com.post.service.SurveyPostService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ASUS
 */
@Service
public class SurveyPostServiceImpl implements SurveyPostService {

    @Autowired
    private SurveyPostRepository surveyRepo;

    @Override
    public List<Surveypost> getSurveyPost(Map<String, String> params) {
        return this.surveyRepo.getSurveyPost(params);
    }

    @Override
    public Surveypost getSurveyPostById(int id) {
        return this.surveyRepo.getSurveyPostById(id);
    }

    @Override
    public Surveypost createOrUpdate(Surveypost sp) {
        return this.surveyRepo.createOrUpdate(sp);
    }

    @Override
    public void deleteSurveyPost(int id) {
        this.surveyRepo.deleteSurveyPost(id);
    }

    @Override
    public List<Surveyquestion> getQuestions(int surveyId) {
        return this.surveyRepo.getQuestions(surveyId);
    }

    @Override
    public List<Map<String, Object>> getListQuestion(int surveyId,Map<String, String> params) {
       return this.surveyRepo.getListQuestion(surveyId,params);
    }

    @Override
    public List<Map<String, Object>> getStats(int surveyId) {
       return this.surveyRepo.getStats(surveyId);
    }

}
