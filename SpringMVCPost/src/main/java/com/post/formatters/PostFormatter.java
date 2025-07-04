/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.formatters;

import com.post.pojo.Post;
import java.text.ParseException;
import java.util.Locale;
import org.springframework.format.Formatter;

/**
 *
 * @author Admin
 */
public class PostFormatter implements Formatter<Post>{
    @Override
    //Controller -> View
    public String print(Post p, Locale locale) {
        return String.valueOf(p.getId()); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    //View ->Controller
    public Post parse(String postId, Locale locale) throws ParseException {
        Post p = new Post();
        p.setId(Integer.valueOf(postId));// Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        return p;
    }
}