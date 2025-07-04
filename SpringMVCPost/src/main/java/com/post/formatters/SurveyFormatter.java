/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.formatters;


import com.post.pojo.Surveypost;
import java.text.ParseException;
import java.util.Locale;
import org.springframework.format.Formatter;

/**
 *
 * @author Admin
 */
public class SurveyFormatter implements Formatter<Surveypost>{
   
    @Override
    //Controller -> View
    public String print(Surveypost p, Locale locale) {
        return String.valueOf(p.getId()); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    //View ->Controller
    public Surveypost parse(String surveyId, Locale locale) throws ParseException {
        Surveypost p = new Surveypost();
        p.setId(Integer.valueOf(surveyId));// Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        return p;
    }

}