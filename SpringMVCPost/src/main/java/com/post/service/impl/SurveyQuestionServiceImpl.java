/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.service.impl;

import com.post.pojo.Surveyoption;
import com.post.pojo.Surveyquestion;
import com.post.repositories.SurveyQuestionRepository;
import com.post.service.SurveyQuestionService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ASUS
 */
@Service
public class SurveyQuestionServiceImpl implements SurveyQuestionService {
     @Autowired
    private SurveyQuestionRepository questionRepo;

    @Override
    public List<Surveyquestion> getQuestion(Map<String, String> params) {
        return this.questionRepo.getQuestion(params);
    }

    @Override
    public Surveyquestion getSurveyQuestionById(int id) {
        return this.questionRepo.getSurveyQuestionById(id);
    }

    @Override
    public Surveyquestion createOrUpdate(Surveyquestion sp) {
        return this.questionRepo.createOrUpdate(sp);
    }

    @Override
    public void deleteSurveyQuestion(int id) {
        this.questionRepo.deleteSurveyQuestion(id);
    }

    @Override
    public List<Surveyoption> getOptions(int questionId) {
        return this.questionRepo.getOptions(questionId);
    }

    @Override
    public long countSurveyOptionsBySurveyId(int surveyId) {
        return this.questionRepo.countSurveyOptionsBySurveyId(surveyId);
    }
}
