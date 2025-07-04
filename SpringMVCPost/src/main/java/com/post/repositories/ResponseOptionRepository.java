/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.post.repositories;

/**
 *
 * @author ASUS
 */
import com.post.pojo.Responseoption;
import com.post.pojo.ResponseoptionPK;
import com.post.pojo.Surveyoption;
import com.post.pojo.User;
import java.util.List;
import java.util.Map;

public interface ResponseOptionRepository {

    List<Responseoption> getResponse(Map<String, String> params);

    Responseoption createOrUpdate(Responseoption r);

    void deleteResponseoption(ResponseoptionPK id);

    Responseoption getResponseoptionId(ResponseoptionPK id);

    List<User> getUserResponse(int optionId);

    List<Surveyoption> getOptionsByUserId(int userId);

    List<Responseoption> getResponsesByUserAndSurvey(int userId, int surveyId);

}
