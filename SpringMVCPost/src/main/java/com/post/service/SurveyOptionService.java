/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.post.service;

import com.post.pojo.Responseoption;
import com.post.pojo.Surveyoption;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ASUS
 */
public interface SurveyOptionService {

    List<Surveyoption> getOption(Map<String, String> params);

    Surveyoption getSurveyoOptionById(int id);

    Surveyoption createOrUpdate(Surveyoption sp);

    void deleteSurveyoption(int id);

    List<Responseoption> getResponseoption(int optionId);

    List<Surveyoption> getOptionsByIds(List<Integer> ids);
}
