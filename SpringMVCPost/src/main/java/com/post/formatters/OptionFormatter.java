/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.formatters;


import com.post.pojo.Surveyoption;
import java.text.ParseException;
import java.util.Locale;
import org.springframework.format.Formatter;

/**
 *
 * @author Admin
 */
public class OptionFormatter implements Formatter<Surveyoption>{
    @Override
    //Controller -> View
    public String print(Surveyoption c, Locale locale) {
        return String.valueOf(c.getId()); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    //View ->Controller
    public Surveyoption parse(String quesId, Locale locale) throws ParseException {
        Surveyoption c = new Surveyoption();
        c.setId(Integer.valueOf(quesId));// Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        return c;
    }
}