/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.controllers;

import com.post.enums.Role;
import com.post.formatters.DateFormatter;
import com.post.pojo.Admin;
import com.post.pojo.Surveypost;
import com.post.pojo.Surveyquestion;
import com.post.pojo.User;
import com.post.service.AdminService;
import com.post.service.SurveyPostService;
import com.post.service.SurveyQuestionService;
import com.post.service.UserService;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ASUS
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiSurveyPostController {

    @Autowired
    private SurveyPostService surveyService;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private SurveyQuestionService questionService;

    @GetMapping("/secure/survey-post/") // ADMIN
    @ResponseBody
    @CrossOrigin
    public ResponseEntity<?> getSurveyPost(Principal principal, @RequestParam Map<String, String> params) {
        User currentUser = this.userService.getUserByUsername(principal.getName());

//        if (!Role.ROLE_ADMIN.getDisplayName().equals(currentUser.getRole())) {
//            return new ResponseEntity<>("You do not have permission for this action", HttpStatus.FORBIDDEN);
//        }
        List<Surveypost> posts = this.surveyService.getSurveyPost(params);
        List<Map<String, Object>> result = posts.stream().map(post -> {
            Map<String, Object> postMap = new HashMap<>();
            postMap.put("id", post.getId());
            postMap.put("title", post.getTitle());
            postMap.put("description", post.getDescription());
            postMap.put("startDate", DateFormatter.format(post.getStartDate()));
            postMap.put("endDate", DateFormatter.format(post.getEndDate()));
            postMap.put("createdAt", DateFormatter.format(post.getCreatedAt()));
            postMap.put("updatedAt", DateFormatter.format(post.getUpdateAt()));
            postMap.put("status", post.getStatus());

            return postMap;
        }).collect(Collectors.toList());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(path = "/secure/survey-post/")
    public ResponseEntity<String> createPost(Principal principal,
            @RequestBody Map<String, String> request) {
        Surveypost post = new Surveypost();
        User u = this.userService.getUserByUsername(principal.getName());
        if (!Role.ROLE_ADMIN.getDisplayName().equals(u.getRole())) {
            return new ResponseEntity<>("Don’t have permission for this action", HttpStatus.FORBIDDEN);
        }

        Admin admin = this.adminService.getAdminById(u.getId());
        post.setAdminId(admin);

        if (request.containsKey("title") && !request.get("title").trim().isEmpty()) {
            post.setTitle(request.get("title"));
        }
        if (request.containsKey("description") && !request.get("description").trim().isEmpty()) {
            post.setDescription(request.get("description"));
        }
        if (request.containsKey("startDate") && !request.get("startDate").trim().isEmpty()) {
            java.sql.Date startDate = java.sql.Date.valueOf(request.get("startDate").trim());
            post.setStartDate(startDate);//yyyy-MM-dd
        }
        if (request.containsKey("endDate") && !request.get("endDate").trim().isEmpty()) {
            java.sql.Date endDate = java.sql.Date.valueOf(request.get("endDate").trim());
            post.setEndDate(endDate);//yyyy-MM-dd
        }
        if (request.containsKey("status") && !request.get("status").trim().isEmpty()) {
            post.setStatus(request.get("status"));
        }

        this.surveyService.createOrUpdate(post);
        return new ResponseEntity<>("Survey Post updated!", HttpStatus.OK);

    }

    @PutMapping(path = "/secure/survey-post/{surveyId}")
    public ResponseEntity<String> updatePost(Principal principal,
            @RequestBody Map<String, String> request,
            @PathVariable(value = "surveyId") int id) {
        Surveypost post = this.surveyService.getSurveyPostById(id);
        if (post == null) {
            return new ResponseEntity("Cannot found survey post!", HttpStatus.NOT_FOUND);
        }
        User u = this.userService.getUserByUsername(principal.getName());
        if (!Role.ROLE_ADMIN.getDisplayName().equals(u.getRole())) {
            return new ResponseEntity<>("Don’t have permission for this action", HttpStatus.FORBIDDEN);
        }
        if (request.containsKey("title") && !request.get("title").trim().isEmpty()) {
            post.setTitle(request.get("title"));
        }
        if (request.containsKey("description") && !request.get("description").trim().isEmpty()) {
            post.setDescription(request.get("description"));
        }
        if (request.containsKey("startDate") && !request.get("startDate").trim().isEmpty()) {
            java.sql.Date startDate = java.sql.Date.valueOf(request.get("startDate").trim());
            post.setStartDate(startDate);//yyyy-MM-dd
        }
        if (request.containsKey("endDate") && !request.get("endDate").trim().isEmpty()) {
            java.sql.Date endDate = java.sql.Date.valueOf(request.get("endDate").trim());
            post.setEndDate(endDate);//yyyy-MM-dd
        }
        if (request.containsKey("status") && !request.get("status").trim().isEmpty()) {
            post.setStatus(request.get("status"));
        }

        this.surveyService.createOrUpdate(post);
        return new ResponseEntity<>("Survey Post updated!", HttpStatus.OK);

    }

    @DeleteMapping("/secure/survey-post/{surveyId}") //ADMIN - xóa hết
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> deletePost(Principal principal,
            @PathVariable(value = "surveyId") int id) {
        Surveypost post = this.surveyService.getSurveyPostById(id);
        if (post == null) {
            return new ResponseEntity("Cannot found survey post!", HttpStatus.NOT_FOUND);
        }
        User currentUser = this.userService.getUserByUsername(principal.getName());
        if (Role.ROLE_ADMIN.getDisplayName().equals(currentUser.getRole())) {
            this.surveyService.deleteSurveyPost(id);
            return new ResponseEntity<>("Deleted!", HttpStatus.OK);
        }
        return new ResponseEntity("You donnot have permission for this action", HttpStatus.FORBIDDEN);
    }

//================= SURVEY ====== QUESTION =====================================================
    @GetMapping("/secure/survey-post/{surveyId}/details") //ADMIN - chi tiết các câu hỏi và trả lời
    @ResponseBody
    @CrossOrigin
    public ResponseEntity<?> getDetailPost(Principal principal,
            @RequestParam Map<String, String> params,
            @PathVariable(value = "surveyId") int id) {
        User currentUser = this.userService.getUserByUsername(principal.getName());
//        if (Role.ROLE_ADMIN.getDisplayName().equals(currentUser.getRole())) {
//            return new ResponseEntity<>(this.surveyService.getListQuestion(id, params), HttpStatus.OK);
//        }
         return new ResponseEntity<>(this.surveyService.getListQuestion(id, params), HttpStatus.OK);
      //  return new ResponseEntity("You donnot have permission for this action", HttpStatus.FORBIDDEN);
    }

    @PostMapping(path = "/secure/survey-post/{surveyId}/create-question")
    public ResponseEntity<String> createQuestion(Principal principal,
            @RequestBody Map<String, String> request,
            @PathVariable(value = "surveyId") int id) {
        Surveypost survey = this.surveyService.getSurveyPostById(id);
        
        Surveyquestion post = new Surveyquestion();
        User u = this.userService.getUserByUsername(principal.getName());
        if (!Role.ROLE_ADMIN.getDisplayName().equals(u.getRole())) {
            return new ResponseEntity<>("Don’t have permission for this action", HttpStatus.FORBIDDEN);
        }
        post.setSurveyId(survey);
        if (request.containsKey("content") && !request.get("content").trim().isEmpty()) {
            post.setContent(request.get("content"));
        }
        if (request.containsKey("responseType") && !request.get("responseType").trim().isEmpty()) {
            post.setResponseType(request.get("responseType"));
        }
        this.questionService.createOrUpdate(post);
        return new ResponseEntity<>("Question Post updated!", HttpStatus.OK);

    }
    //====================== STATS ===========================================
      @GetMapping("/secure/survey-post/{surveyId}/stats") //ADMIN - chi tiết các câu hỏi và trả lời
    @ResponseBody
    @CrossOrigin
    public ResponseEntity<?> getUsers(Principal principal,
            @PathVariable(value = "surveyId") int id) {
        User currentUser = this.userService.getUserByUsername(principal.getName());
        if (Role.ROLE_ADMIN.getDisplayName().equals(currentUser.getRole())) {
            return new ResponseEntity<>(this.surveyService.getStats(id), HttpStatus.OK);
        }
        return new ResponseEntity("You donnot have permission for this action", HttpStatus.FORBIDDEN);
    }

}
