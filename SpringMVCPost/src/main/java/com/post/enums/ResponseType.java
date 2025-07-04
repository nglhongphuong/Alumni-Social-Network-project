/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.post.enums;

/**
 *
 * @author ASUS
 */
public enum ResponseType {
    SINGLE_CHOICE("SINGLE_CHOICE"),
    MULTIPLE_CHOICE("MULTIPLE_CHOICE"),
    TEXT("TEXT");
    private final String displayName;
    
    ResponseType(String displayName)
    {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
    
}
