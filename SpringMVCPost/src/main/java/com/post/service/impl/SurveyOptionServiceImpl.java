/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.service.impl;

import com.post.pojo.Responseoption;
import com.post.pojo.Surveyoption;
import com.post.repositories.SurveyOptionRepository;
import com.post.service.SurveyOptionService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ASUS
 */
@Service
public class SurveyOptionServiceImpl implements SurveyOptionService{
    @Autowired
    private SurveyOptionRepository optionRepo;
    
    @Override
    public List<Surveyoption> getOption(Map<String, String> params) {
        return this.optionRepo.getOption(params);     
    }

    @Override
    public Surveyoption getSurveyoOptionById(int id) {
        return this.optionRepo.getSurveyoOptionById(id);
    }

    @Override
    public Surveyoption createOrUpdate(Surveyoption sp) {
        return this.optionRepo.createOrUpdate(sp);
    }
    @Override
    public void deleteSurveyoption(int id) {
         this.optionRepo.deleteSurveyoption(id);
    }

    @Override
    public List<Responseoption> getResponseoption(int optionId) {
        return this.optionRepo.getResponseoption(optionId);
    }

    @Override
    public List<Surveyoption> getOptionsByIds(List<Integer> ids) {
        return this.optionRepo.getOptionsByIds(ids);
    }
}
