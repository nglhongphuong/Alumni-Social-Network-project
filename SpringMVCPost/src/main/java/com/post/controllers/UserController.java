/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.controllers;

import com.post.enums.Gender;
import com.post.enums.Role;
import com.post.pojo.User;
import com.post.service.UserService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ASUS
 */

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginView() {
        return "userPage/login";
    }

    @RequestMapping("/user/")
    public String userView(Model model, @RequestParam Map<String, String> params) {
        model.addAttribute("user", this.userService.getUsers(params));
        return "userPage/user";
    }

    @GetMapping("/user/createUser")
    public String createView(Model model, @RequestParam Map<String, String> params) {
        model.addAttribute("user", new User());
        model.addAttribute("gender", Gender.values());
        model.addAttribute("role", Role.values());
        return "userPage/createUser";
    }

    @PostMapping("/user/add-user")
    public String createUser(@RequestParam Map<String, String> params,
            @RequestParam("avatar") MultipartFile avatar,
            @RequestParam("coverPhoto") MultipartFile coverPhoto) {
        this.userService.addUser(params, avatar, coverPhoto);
        return "redirect:/user/";
    }
    
    @GetMapping("/user/{userId}")
    public String updateView(Model model, @PathVariable(value = "userId") int id) {
        model.addAttribute("gender", Gender.values());
        model.addAttribute("role", Role.values());
         model.addAttribute("user",this.userService.getUserById(id));
        return "userPage/updateUser";
    }
    
    @PostMapping("/user/update-user")
    public String updateUser(@RequestParam Map<String, String> params,
            @RequestParam("avatar") MultipartFile avatar,
            @RequestParam("coverPhoto") MultipartFile coverPhoto) {
        this.userService.updateUser(params, avatar, coverPhoto);
        return "redirect:/user/";
    }
    
   
}
