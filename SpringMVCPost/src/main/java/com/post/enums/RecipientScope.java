/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.post.enums;

/**
 *
 * @author ASUS
 */
public enum RecipientScope {
    INDIVIDUAL("INDIVIDUAL"),
    ROLE_GROUP("ROLE_GROUP"),
    CUSTOM_GROUP("CUSTOM_GROUP"),
    ALL("ALL");
    private final String displayName;

    RecipientScope(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
