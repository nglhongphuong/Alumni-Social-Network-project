/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.controllers;

import com.post.enums.ResponseType;
import com.post.pojo.Surveypost;
import com.post.service.SurveyPostService;
import com.post.service.SurveyQuestionService;
import com.post.service.UserService;
import com.post.enums.Status;
import com.post.pojo.Surveyoption;
import com.post.pojo.Surveyquestion;
import com.post.service.AdminService;
import com.post.service.SurveyOptionService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
public class SurveypostController {

    @Autowired
    private SurveyPostService surveyService;

    @Autowired
    private SurveyQuestionService questionService;

    @Autowired
    private SurveyOptionService optionService;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @RequestMapping("/")
    public String surveyView(Model model, @RequestParam Map<String, String> params) {
        List<Surveypost> surveys = this.surveyService.getSurveyPost(params);

        Map<Integer, Long> countQuestion = new HashMap<>();
        for (Surveypost s : surveys) {
            long countques = this.questionService.countSurveyOptionsBySurveyId(s.getId());
            countQuestion.put(s.getId(), countques);
        }

        model.addAttribute("surveys", surveys);
        model.addAttribute("countQuestion", countQuestion);
//        model.addAttribute("countReactions", countReactions);
        return "surveyView/survey";
    }

    @GetMapping("/createSurvey")
    public String createView(Model model, @RequestParam Map<String, String> params) {

        model.addAttribute("admin", this.adminService.getAdmins(params));
        model.addAttribute("status", Status.values());
        model.addAttribute("survey", new Surveypost());
        return "surveyView/createSurvey";
    }
@PostMapping("/add-survey")
public String createPost(
    @ModelAttribute("survey") Surveypost p,
    @RequestParam("startDateStr") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate startDate,
    @RequestParam("endDateStr") @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate endDate
) {
    System.out.println("StartDate: " + startDate);
    System.out.println("EndDate: " + endDate);

    // Chuyển LocalDate sang java.util.Date
    Date startDateDate = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    Date endDateDate = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

    // Gán vào entity
    p.setStartDate(startDateDate);
    p.setEndDate(endDateDate);

    this.surveyService.createOrUpdate(p);


    return "redirect:/surveypost/" + p.getId() + "/question";
}



    //    =============== UPDATE SURVEY =====================
    @GetMapping("/{surveyId}")
    public String updateView(Model model,@PathVariable(value = "surveyId") int id, @RequestParam Map<String, String> params) {

        model.addAttribute("admin", this.adminService.getAdmins(params));
        model.addAttribute("status", Status.values());
        model.addAttribute("survey", this.surveyService.getSurveyPostById(id));
        
        return "surveyView/createSurvey";
    }
    
    
    @GetMapping("/delete")
    public String deleteSurvey(@RequestParam("id")int id){
    
        this.surveyService.deleteSurveyPost(id);
        return "redirect:/surveypost/";
    }
    
    
//    QUESTION FORM
    @GetMapping("/{surveyId}/question")

    public String questionView(@PathVariable(value = "surveyId") int surveyId, Model model,
            @RequestParam Map<String, String> params) {
        // Load câu hỏi của khảo sát có id = surveyId
        Map<String, String> surveyParams = new HashMap<>(params);
        surveyParams.put("surveyId", String.valueOf(surveyId));

        Surveypost survey = this.surveyService.getSurveyPostById(surveyId);
        List<Surveyoption> options = this.optionService.getOption(params);

        model.addAttribute("responseTypes", ResponseType.values());
        model.addAttribute("options", this.optionService.getOption(params));
        model.addAttribute("questions", this.questionService.getQuestion(surveyParams));
        model.addAttribute("question", new Surveyquestion());
        model.addAttribute("survey", survey);
        model.addAttribute("option", new Surveyoption());
        return "surveyView/question";
    }

    
//    ==========================  DELETE QUESTION ================
   @GetMapping("/{surveyId}/question/delete")
public String deleteQuestion(@RequestParam("id") int id,
                             @PathVariable("surveyId") int surveyId) {
    questionService.deleteSurveyQuestion(id);
        return "redirect:/surveypost/"+surveyId +"/question";
    }
    
//    ================ UPDATE QUESTION =====================
     @GetMapping("/{surveyId}/question/{questionId}")
    public String updateQuestion(@PathVariable(value = "surveyId") int surveyId, Model model,
            @RequestParam Map<String, String> params,
            @PathVariable("questionId") int questionId) {
        // Load câu hỏi của khảo sát có id = surveyId
        Map<String, String> surveyParams = new HashMap<>(params);
        surveyParams.put("surveyId", String.valueOf(surveyId));

        Surveypost survey = this.surveyService.getSurveyPostById(surveyId);
        List<Surveyoption> options = this.optionService.getOption(params);

        model.addAttribute("responseTypes", ResponseType.values());
        model.addAttribute("options", this.optionService.getOption(params));
        model.addAttribute("questions", this.questionService.getQuestion(surveyParams));
        model.addAttribute("question", this.questionService.getSurveyQuestionById(questionId));
        model.addAttribute("survey", survey);
        model.addAttribute("option", new Surveyoption());
     
        return "surveyView/question";
    
    }
    
//    =================== POST QUESTION =============
    @PostMapping("/{surveyId}/question/add-question")
    public String createQuestion(
            @PathVariable("surveyId") int surveyId,
            @ModelAttribute("question") Surveyquestion question,
            HttpServletRequest request
    ) {
        Surveypost survey = this.surveyService.getSurveyPostById(surveyId);
        question.setSurveyId(survey);

        this.questionService.createOrUpdate(question);
        return "redirect:/surveypost/" + surveyId + "/question";
    }
    
//    ================ POST OPTION ============================
    @PostMapping("/{surveyId}/question/{questionId}/add-option")
    public String createOption(
            @PathVariable("surveyId") int surveyId,
            @PathVariable("questionId") int questionId,
            @ModelAttribute("option") Surveyoption option) {
        Surveyquestion question = this.questionService.getSurveyQuestionById(questionId);
        option.setQuestionId(question);

        this.optionService.createOrUpdate(option);
        
        return "redirect:/surveypost/" + surveyId + "/question";
    }
    
 //    ================ UPDATE OPTION ============================
     @GetMapping("/{surveyId}/question/{questionId}/option/{optionId}")
    public String updateOption(@PathVariable(value = "surveyId") int surveyId, Model model,
            @RequestParam Map<String, String> params,
            @PathVariable("questionId") int questionId,
            @PathVariable(value = "optionId") int optionId) {
        // Load câu hỏi của khảo sát có id = surveyId
        Map<String, String> surveyParams = new HashMap<>(params);
        surveyParams.put("surveyId", String.valueOf(surveyId));

        Surveypost survey = this.surveyService.getSurveyPostById(surveyId);
        
        model.addAttribute("responseTypes", ResponseType.values());
        model.addAttribute("options", this.optionService.getOption(params));
        model.addAttribute("questions", this.questionService.getQuestion(surveyParams));
        model.addAttribute("question", this.questionService.getSurveyQuestionById(questionId));
        model.addAttribute("survey", survey);
        model.addAttribute("option", this.optionService.getSurveyoOptionById(optionId));
     
        return "surveyView/question";
    
    }
 //    ================ DELETE OPTION ============================
     @GetMapping("/{surveyId}/question/{questionId}/option/delete")
public String deleteOption(@RequestParam("id") int id,
                             @PathVariable("surveyId") int surveyId,
                             @PathVariable("questionId") int questionId) {
        this.optionService.deleteSurveyoption(id);
        return "redirect:/surveypost/"+surveyId +"/question";
    }
    
    

//    @GetMapping("/{surveyId}/question/{questionId}")
//    public String updateQuestionView(@PathVariable("questionId")int questionId, @PathVariable(value = "surveyId") 
//            int surveyId, Model model,  
//            @RequestParam Map<String, String> params){
//    
//        // Load câu hỏi của khảo sát có id = surveyId
//        Map<String, String> surveyParams = new HashMap<>(params);
//        surveyParams.put("surveyId", String.valueOf(surveyId));
//        
//        //load option theo question
//        Map<String, String> questionParams = new HashMap<>(params);
//        questionParams.put("questionId", String.valueOf(questionId));
//
//        Surveypost survey = this.surveyService.getSurveyPostById(surveyId);
//        List<Surveyoption> options = this.optionService.getOption(questionParams);
//
//// In ra danh sách options (nội dung và questionId)
//        for (Surveyoption opt : options) {
//            System.out.printf("Option ID=%d, Content='%s', Question ID=%d%n",
//                    opt.getId(),
//                    opt.getContent(),
//                    opt.getQuestionId() != null ? opt.getQuestionId().getId() : null);
//        }
//
//        model.addAttribute("responseTypes", ResponseType.values());
//        model.addAttribute("options", options);
//        model.addAttribute("questions", this.questionService.getQuestion(surveyParams));
//        model.addAttribute("question", this.questionService.getSurveyQuestionById(questionId)); 
//        
//        model.addAttribute("survey", survey);
//        return "surveyView/question";
//    }

}