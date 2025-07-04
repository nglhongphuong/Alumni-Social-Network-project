/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.post.enums;

/**
 *
 * @author ASUS
 */
public enum Role {
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_ALUMNI("ROLE_ALUMNI"), //cuu sinh vien
    ROLE_LECTURER("ROLE_LECTURER");//Giang vien
    private final String displayName;
    
    Role(String displayName)
    {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
}
