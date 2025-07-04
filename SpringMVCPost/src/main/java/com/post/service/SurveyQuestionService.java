/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.post.service;

/**
 *
 * @author ASUS
 */
import com.post.pojo.Surveyoption;
import com.post.pojo.Surveyquestion;
import java.util.List;
import java.util.Map;

public interface SurveyQuestionService {

    List<Surveyquestion> getQuestion(Map<String, String> params);

    Surveyquestion getSurveyQuestionById(int id);

    Surveyquestion createOrUpdate(Surveyquestion sp);

    void deleteSurveyQuestion(int id);

    List<Surveyoption> getOptions(int questionId);
    
     long countSurveyOptionsBySurveyId(int surveyId) ;
}
