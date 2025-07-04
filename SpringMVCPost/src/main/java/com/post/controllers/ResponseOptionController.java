/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.controllers;

import com.post.enums.ResponseType;
import com.post.pojo.Responseoption;
import com.post.pojo.Surveyoption;
import com.post.pojo.User;
import com.post.service.ResponseOptionService;
import com.post.service.SurveyOptionService;
import com.post.service.SurveyPostService;
import com.post.service.SurveyQuestionService;
import com.post.service.UserService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Admin
 */
@Controller
@RequestMapping("/surveypost")
public class ResponseOptionController {

    @Autowired
    private SurveyPostService surveyService;

    @Autowired
    private SurveyQuestionService questionService;

    @Autowired
    private SurveyOptionService optionService;

    @Autowired
    private UserService userService;

    @Autowired
    private ResponseOptionService responseService;

    @RequestMapping("/{surveyId}/responseOption")
    public String responseView(Model model, @PathVariable(value = "surveyId") int surveyId,
            @RequestParam Map<String, String> params) {

        // Load câu hỏi của khảo sát có id = surveyId
        Map<String, String> surveyParams = new HashMap<>(params);
        surveyParams.put("surveyId", String.valueOf(surveyId));

        List<Surveyoption> options = this.optionService.getOption(params);
        Map<Integer, List<Surveyoption>> optionsMap = options.stream()
                .collect(Collectors.groupingBy(opt -> opt.getQuestionId().getId()));

        model.addAttribute("user", this.userService.getUsers(params));
        model.addAttribute("survey", this.surveyService.getSurveyPostById(surveyId));
        model.addAttribute("responseTypes", ResponseType.values());
        model.addAttribute("options", this.optionService.getOption(params));
        model.addAttribute("questions", this.questionService.getQuestion(surveyParams));
        model.addAttribute("optionsMap", optionsMap);

        return "surveyView/responseoption";
    }
//     ================== ADD RESPONSE ======================

    @PostMapping("/{surveyId}/responseOption/add-response")
    public String createUpdateResponse(
            @PathVariable("surveyId") int surveyId,
            @RequestParam("optionIds") List<Integer> optionIds,
            @RequestParam("userResponse") int userId) {
        
        System.out.println("surveyId = " + surveyId);
        System.out.println("userId = " + userId);
        User user = this.userService.getUserById(userId);

        for (Integer optionId : optionIds) {
            Responseoption r = new Responseoption(userId, optionId);
            r.setUser(user);

            Surveyoption surveyoption = this.optionService.getSurveyoOptionById(optionId);
            r.setSurveyoption(surveyoption);

            this.responseService.createOrUpdate(r);
        }

        return  "redirect:/surveypost/"+surveyId+"/responseOption";
        

    }
    
//    ====================== UPDATE RESPONSE ======================
//    @GetMapping("/{surveyId}/responseOption/{userId}")
//    public String updateResponse(Model model, 
//            @PathVariable(value = "surveyId") int surveyId,
//            @RequestParam Map<String, String> params,
//            @PathVariable("userResponse") int userId) {
//
//        System.err.println("userResponse: "+userId);
//        // Load câu hỏi của khảo sát có id = surveyId
//        Map<String, String> surveyParams = new HashMap<>(params);
//        surveyParams.put("surveyId", String.valueOf(surveyId));
//
//        List<Surveyoption> options = this.optionService.getOption(params);
//        Map<Integer, List<Surveyoption>> optionsMap = options.stream()
//                .collect(Collectors.groupingBy(opt -> opt.getQuestionId().getId()));
//
//        model.addAttribute("userResponse", this.userService.getUserById(userId));
//        model.addAttribute("survey", this.surveyService.getSurveyPostById(surveyId));
//        model.addAttribute("responseTypes", ResponseType.values());
//        model.addAttribute("options", this.optionService.getOption(params));
//        model.addAttribute("questions", this.questionService.getQuestion(surveyParams));
//        model.addAttribute("optionsMap", optionsMap);
//        model.addAttribute("optionResponse", this.responseService.getOptionsByUserId(userId));
//
//        return "surveyView/responseoption";
//    }
//    
}