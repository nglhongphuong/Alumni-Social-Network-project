/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.formatters;


import com.post.pojo.Surveyquestion;
import java.text.ParseException;
import java.util.Locale;
import org.springframework.format.Formatter;

/**
 *
 * @author Admin
 */
public class QuestionFormatter implements Formatter<Surveyquestion>{
    

    @Override
    //Controller -> View
    public String print(Surveyquestion c, Locale locale) {
        return String.valueOf(c.getId()); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    //View ->Controller
    public Surveyquestion parse(String quesId, Locale locale) throws ParseException {
        Surveyquestion c = new Surveyquestion();
        c.setId(Integer.valueOf(quesId));// Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        return c;
    }
    

}
