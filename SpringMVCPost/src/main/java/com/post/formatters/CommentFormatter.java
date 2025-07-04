/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.formatters;
import com.post.pojo.Comment;
import java.text.ParseException;
import java.util.Locale;
import org.springframework.format.Formatter;

/**
 *
 * @author Admin
 */
public class CommentFormatter implements Formatter<Comment>{

    @Override
    //Controller -> View
    public String print(Comment c, Locale locale) {
        return String.valueOf(c.getId()); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    //View ->Controller
    public Comment parse(String comId, Locale locale) throws ParseException {
        Comment c = new Comment();
        c.setId(Integer.valueOf(comId));// Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        return c;
    }
    
}