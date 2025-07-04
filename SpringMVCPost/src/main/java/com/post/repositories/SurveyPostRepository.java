/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.post.repositories;

/**
 *
 * @author ASUS
 */
import com.post.pojo.Surveypost;
import com.post.pojo.Surveyquestion;
import java.util.List;
import java.util.Map;


public interface SurveyPostRepository {
     List<Surveypost> getSurveyPost(Map<String, String> params);
     List<Map<String, Object>> getListQuestion(int surveyId,Map<String, String> params);
    Surveypost getSurveyPostById (int id);
    Surveypost createOrUpdate(Surveypost sp);
    void deleteSurveyPost(int id);
    List<Surveyquestion> getQuestions(int surveyId);
    List<Map<String, Object>> getStats(int surveyId);
}
