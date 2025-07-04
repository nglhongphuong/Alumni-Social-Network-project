/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.formatters;

import com.post.pojo.Admin;
import java.text.ParseException;
import java.util.Locale;
import org.springframework.format.Formatter;

/**
 *
 * @author Admin
 */
public class AdminFormatter implements Formatter<Admin>{
    

    @Override
    public String print(Admin admin, Locale locale) {
        return String.valueOf(admin.getUserId());
    }

    @Override
    public Admin parse(String adminId, Locale locale) throws ParseException {
        Admin a = new Admin();
        a.setUserId(Integer.valueOf(adminId));
        return a;
    }
}