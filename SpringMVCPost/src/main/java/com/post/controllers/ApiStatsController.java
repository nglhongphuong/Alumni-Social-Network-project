/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.controllers;

import com.post.service.StatsService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ASUS
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiStatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/secure/stats/users")
    public ResponseEntity<List<Map<String, Object>>> getUserStats() {
        List<Map<String, Object>> stats = statsService.getUserStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/secure/stats/posts")
    public ResponseEntity<List<Map<String, Object>>> getPostStats() {
        List<Map<String, Object>> stats = statsService.getPostStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/secure/stats/survey-posts")
    public ResponseEntity<List<Map<String, Object>>> getSurveyPostStats() {
        List<Map<String, Object>> stats = statsService.getSurveyPostStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/secure/stats/invitation-posts")
    public ResponseEntity<List<Map<String, Object>>> getInvitationPostStats() {
        List<Map<String, Object>> stats = statsService.getInvitationPostStats();
        return ResponseEntity.ok(stats);
    }
}
