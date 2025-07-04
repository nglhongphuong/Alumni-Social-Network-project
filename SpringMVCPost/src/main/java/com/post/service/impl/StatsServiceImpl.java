/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.service.impl;

import com.post.repositories.StatsRepository;
import com.post.service.StatsService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ASUS
 */
@Service
public class StatsServiceImpl implements StatsService{
    @Autowired
    private StatsRepository statsRepo;

    @Override
    public List<Map<String, Object>> getUserStats() {
        return this.statsRepo.getUserStats();
    }

    @Override
    public List<Map<String, Object>> getPostStats() {
        return this.statsRepo.getPostStats();
    }

    @Override
    public List<Map<String, Object>> getSurveyPostStats() {
        return this.statsRepo.getSurveyPostStats();
    }

    @Override
    public List<Map<String, Object>> getInvitationPostStats() {
        return this.statsRepo.getInvitationPostStats();
    }
    
}
