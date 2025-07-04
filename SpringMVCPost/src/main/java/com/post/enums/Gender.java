/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.post.enums;

/**
 *
 * @author ASUS
 */
public enum Gender {
    MALE("male"),
    FEMALE("female");
    private final String displayName;
    
    Gender(String displayName)
    {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
}
