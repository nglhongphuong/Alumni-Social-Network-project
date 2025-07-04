/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.post.service;

import java.util.List;
import java.util.Map;

/**
 *
 * @author ASUS
 */
public interface StatsService {

    List<Map<String, Object>> getUserStats();

    List<Map<String, Object>> getPostStats();

    List<Map<String, Object>> getSurveyPostStats();

    List<Map<String, Object>> getInvitationPostStats();
}
