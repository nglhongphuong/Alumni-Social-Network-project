/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.controllers;

import com.post.enums.Role;
import com.post.pojo.Responseoption;
import com.post.pojo.ResponseoptionPK;
import com.post.pojo.Surveyoption;
import com.post.pojo.User;
import com.post.service.ResponseOptionService;
import com.post.service.SurveyOptionService;
import com.post.service.UserService;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ASUS
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiResponseOptionController {

    @Autowired
    private UserService userService;

    @Autowired
    private ResponseOptionService reponseService;

    @Autowired
    private SurveyOptionService optionService;

    @PostMapping("/secure/survey-response")
    public ResponseEntity<String> submitSurveyResponse(
            Principal principal,
            @RequestBody Map<String, String> request) {
        String optionIdsString = request.get("optionIds");
        List<Integer> optionIds = Arrays.stream(optionIdsString.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        User currentUser = this.userService.getUserByUsername(principal.getName());

        if (optionIds == null || optionIds.isEmpty()) {
            return new ResponseEntity<>("No options selected!", HttpStatus.BAD_REQUEST);
        }

        for (Integer optionId : optionIds) {
            Surveyoption option = this.optionService.getSurveyoOptionById(optionId);

            if (option != null) {
                Responseoption response = new Responseoption();
                response.setUser(currentUser);
                response.setSurveyoption(option);

                // Tạo khóa chính
                ResponseoptionPK pk = new ResponseoptionPK();
                pk.setUserId(currentUser.getId());
                pk.setOptionId(option.getId());
                response.setResponseoptionPK(pk);

                System.out.println("Bạn đã tới bước này ");
                this.reponseService.createOrUpdate(response);
            }
        }
        return new ResponseEntity<>("Survey response saved!", HttpStatus.OK);
    }

    //Không làm put hơi ....chưa fix
    @GetMapping("/secure/survey-response/{optionId}") //ADMIN - chi tiết các câu hỏi và trả lời
    @ResponseBody
    @CrossOrigin
    public ResponseEntity<?> getStats(Principal principal,
            @PathVariable(value = "optionId") int id) {
        User currentUser = this.userService.getUserByUsername(principal.getName());
        if (Role.ROLE_ADMIN.getDisplayName().equals(currentUser.getRole())) {
            return new ResponseEntity<>(this.reponseService.getUserResponse(id), HttpStatus.OK);
        }
        return new ResponseEntity("You donnot have permission for this action", HttpStatus.FORBIDDEN);
    }

}
