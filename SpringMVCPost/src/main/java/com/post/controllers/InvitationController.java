/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.controllers;

import com.post.enums.RecipientScope;
import com.post.enums.Role;
import com.post.pojo.Invitationpost;
import com.post.pojo.User;
import com.post.service.AdminService;
import com.post.service.InvitationPostService;
import com.post.service.UserService;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/invitation")
public class InvitationController {

    @Autowired
    private InvitationPostService inviService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public String InvitationView(Model model, @RequestParam Map<String, String> params) {
        List<Invitationpost> invitations = this.inviService.getInvitationPosts(params);

        model.addAttribute("invitations", invitations);

        return "invitationView/invitation";
    }

    @GetMapping("/createInvitation")
    public String createView(Model model, @RequestParam Map<String, String> params) {

//        List<User> users = this.userService.getUsers(params);
        List<User> lecturer = this.userService.getUsersByRole("ROLE_LECTURER");
        List<User> alumni = this.userService.getUsersByRole("ROLE_ALUMNI");
        Map<String, List<User>> usersByRole = userService.getAllUsersGroupedByRole();

        for (Map.Entry<String, List<User>> entry : usersByRole.entrySet()) {
            System.out.println("Role: " + entry.getKey());
            List<User> users = entry.getValue();
            for (User user : users) {
                System.out.println("  User: " + user); // đảm bảo lớp User đã override toString()
            }
        }

        model.addAttribute("admin", this.adminService.getAdmins(params));
        model.addAttribute("recipientScope", RecipientScope.values());
        model.addAttribute("invitation", new Invitationpost());
        model.addAttribute("scope", RecipientScope.values());
        model.addAttribute("allUsersByRole",usersByRole );
        model.addAttribute("lecturers", lecturer);
        model.addAttribute("alumnis", alumni);
        model.addAttribute("roles", Role.values());
        return "invitationView/createInvitation";
    }
//============== ADD INVITATION ====================
    @PostMapping("/add-invitation")
    public String createInvitation(@ModelAttribute(value = "invitation") Invitationpost invitation,
                                   @RequestParam("userIds") List<Integer> userIds) {
        Set<User> selectdUsers = new HashSet<>(this.userService.getUsersByIds(userIds));
        invitation.setUserSet(selectdUsers);
        this.inviService.createOrUpdate(invitation);

        // Redirect đến trang câu hỏi
        return "redirect:/";
    }
    
//    ================ UPDATE INVITATION ==================
    @GetMapping("/{invitationId}")
    public String createView(Model model,@PathVariable(value = "invitationId") int id , @RequestParam Map<String, String> params) {

        List<User> lecturer = this.userService.getUsersByRole("ROLE_LECTURER");
        List<User> alumni = this.userService.getUsersByRole("ROLE_ALUMNI");
        Map<String, List<User>> usersByRole = userService.getAllUsersGroupedByRole();

        for (Map.Entry<String, List<User>> entry : usersByRole.entrySet()) {
            System.out.println("Role: " + entry.getKey());
            List<User> users = entry.getValue();
            for (User user : users) {
                System.out.println("  User: " + user); // đảm bảo lớp User đã override toString()
            }
        }

        model.addAttribute("admin", this.adminService.getAdmins(params));
        model.addAttribute("recipientScope", RecipientScope.values());
        model.addAttribute("invitation", this.inviService.getInvitationPostById(id));
        model.addAttribute("scope", RecipientScope.values());
        model.addAttribute("allUsersByRole",usersByRole );
        model.addAttribute("lecturers", lecturer);
        model.addAttribute("alumnis", alumni);
        model.addAttribute("roles", Role.values());
        return "invitationView/createInvitation";
    }
    
    
//    ================ DELETE INVITATION ==================
    @GetMapping("/delete")
    public String deleteInvitation(@RequestParam("id") int id) {

        this.inviService.deleteInvitationPost(id);
        return "redirect:/invitation/";
    }
}