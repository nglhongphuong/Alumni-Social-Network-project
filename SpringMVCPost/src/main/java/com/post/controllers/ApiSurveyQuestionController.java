/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.controllers;

import com.post.enums.ResponseType;
import com.post.enums.Role;
import com.post.pojo.Surveyoption;
import com.post.pojo.Surveypost;
import com.post.pojo.Surveyquestion;
import com.post.pojo.User;
import com.post.service.SurveyOptionService;
import com.post.service.SurveyQuestionService;
import com.post.service.UserService;
import java.security.Principal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ASUS
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiSurveyQuestionController {

    @Autowired
    private SurveyQuestionService questionService;

    @Autowired
    private SurveyOptionService optionService;

    @Autowired
    private UserService userService;

    @DeleteMapping("/secure/survey-question/{questionId}") //ADMIN - xóa hết
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> deletePost(Principal principal,
            @PathVariable(value = "questionId") int id) {
        Surveyquestion post = this.questionService.getSurveyQuestionById(id);
        if (post == null) {
            return new ResponseEntity("Cannot found survey post!", HttpStatus.NOT_FOUND);
        }
        User currentUser = this.userService.getUserByUsername(principal.getName());
        if (Role.ROLE_ADMIN.getDisplayName().equals(currentUser.getRole())) {
            this.questionService.deleteSurveyQuestion(id);
            return new ResponseEntity<>("Deleted!", HttpStatus.OK);
        }
        return new ResponseEntity("You donnot have permission for this action", HttpStatus.FORBIDDEN);
    }

    @PutMapping(path = "/secure/survey-question/{questionId}")
    public ResponseEntity<String> createQuestion(Principal principal,
            @RequestBody Map<String, String> request,
            @PathVariable(value = "questionId") int id) {
        Surveyquestion post = this.questionService.getSurveyQuestionById(id);
        User u = this.userService.getUserByUsername(principal.getName());
        if (!Role.ROLE_ADMIN.getDisplayName().equals(u.getRole())) {
            return new ResponseEntity<>("Don’t have permission for this action", HttpStatus.FORBIDDEN);
        }
        if (request.containsKey("content") && !request.get("content").trim().isEmpty()) {
            post.setContent(request.get("content"));
        }
        if (request.containsKey("responseType") && !request.get("responseType").trim().isEmpty()) {
            post.setResponseType(request.get("responseType"));
        }
        this.questionService.createOrUpdate(post);
        return new ResponseEntity<>("Question Post updated!", HttpStatus.OK);
    }

//======================= CREATE OPTION ================================================
    @PostMapping(path = "/secure/survey-question/{questionId}/create-option")
    public ResponseEntity<String> createOption(Principal principal,
            @RequestBody Map<String, String> request,
            @PathVariable(value = "questionId") int id) {
        Surveyquestion survey = this.questionService.getSurveyQuestionById(id);

        Surveyoption post = new Surveyoption();
        User u = this.userService.getUserByUsername(principal.getName());
        if (!Role.ROLE_ADMIN.getDisplayName().equals(u.getRole())) {
            return new ResponseEntity<>("Don’t have permission for this action", HttpStatus.FORBIDDEN);
        }
        post.setQuestionId(survey);

        if (request.containsKey("content") && !request.get("content").trim().isEmpty()) {
            post.setContent(request.get("content"));
        }
        //Trường hợp text chưa xử lý =)))))
        this.optionService.createOrUpdate(post);
        return new ResponseEntity<>("Option Post created!", HttpStatus.OK);
    }

}
