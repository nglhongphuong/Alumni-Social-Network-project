/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.controllers;

import com.post.enums.RecipientScope;
import com.post.enums.Role;
import com.post.formatters.DateFormatter;
import com.post.pojo.Admin;
import com.post.pojo.Invitationpost;
import com.post.pojo.Post;
import com.post.pojo.User;
import com.post.service.AdminService;
import com.post.service.InvitationPostService;
import com.post.service.UserService;
import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

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
public class ApiInvitationPostController {

    @Autowired
    InvitationPostService invitationService;

    @Autowired
    AdminService adminService;

    @Autowired
    private UserService userService;

//    @GetMapping("/secure/invitation-post/") //ADMIN
//    @ResponseBody
//    @CrossOrigin
//    public ResponseEntity<List<Invitationpost>> getInivitationPost(Principal principal, @RequestParam Map<String, String> params) {
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//        User currentUser = this.userService.getUserByUsername(principal.getName());
//        if (Role.ROLE_ADMIN.getDisplayName().equals(currentUser.getRole())) {
//            return new ResponseEntity<>(this.invitationService.getInvitationPosts(params), HttpStatus.OK);
//        }
//        return new ResponseEntity("You donnot have permission for this action", HttpStatus.FORBIDDEN);
//    }
    @GetMapping("/secure/invitation-post/{invitationId}/user") //ADMIN
    @ResponseBody
    @CrossOrigin
    public ResponseEntity<Map<String, Object>> getUserRecipient(
            @PathVariable(value = "invitationId") int id,
            Principal principal,
            @RequestParam Map<String, String> params) {
        User currentUser = this.userService.getUserByUsername(principal.getName());
        if (Role.ROLE_ADMIN.getDisplayName().equals(currentUser.getRole())) {
            return new ResponseEntity(this.invitationService.getInvitationRecipients(id, params), HttpStatus.OK);
        }
        return new ResponseEntity("You donnot have permission for this action", HttpStatus.FORBIDDEN);
    }

    @PostMapping(path = "/secure/invitation-post/")
    public ResponseEntity<String> createInvitation(Principal principal,
            @RequestBody Map<String, String> request) {

        User u = this.userService.getUserByUsername(principal.getName());
        if (!Role.ROLE_ADMIN.getDisplayName().equals(u.getRole())) {
            return new ResponseEntity<>("Don’t have permission for this action", HttpStatus.FORBIDDEN);
        }

        Invitationpost in = new Invitationpost();
        Admin admin = this.adminService.getAdminById(u.getId());
        in.setAdminId(admin);

        if (request.containsKey("title") && !request.get("title").trim().isEmpty()) {
            in.setTitle(request.get("title"));
        } else {
            return new ResponseEntity<>("Title is empty!", HttpStatus.BAD_REQUEST);
        }

        if (request.containsKey("content") && !request.get("content").trim().isEmpty()) {
            in.setContent(request.get("content"));
        } else {
            return new ResponseEntity<>("Content is empty!", HttpStatus.BAD_REQUEST);
        }

        if (request.containsKey("recipientScope") && !request.get("recipientScope").trim().isEmpty()) {
            String recipientScope = request.get("recipientScope");

            //trên reactjs có thể chọn group user đẩy về [id, id2, id3, ...]
            if (RecipientScope.CUSTOM_GROUP.getDisplayName().equals(recipientScope)) {
                if (request.containsKey("user") && !request.get("user").trim().isEmpty()) {
                    String userIdsRaw = request.get("user");
                    List<Integer> userIds = Arrays.stream(userIdsRaw.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());
                    Set<User> selectedUsers = new HashSet<>();
                    for (Integer userId : userIds) {
                        User user = this.userService.getUserById(userId);
                        if (user != null) {
                            selectedUsers.add(user);
                        }
                    }
                    in.setUserSet(selectedUsers);
                    System.out.println(selectedUsers);
                } else {
                    return new ResponseEntity<>("User list is empty!", HttpStatus.BAD_REQUEST);
                }
            }

            if (RecipientScope.ROLE_GROUP.getDisplayName().equals(recipientScope)) {
                if (request.containsKey("role") && !request.get("role").trim().isEmpty()) {
                    String role = request.get("role");
                    System.out.println("role: " + role);
                    List<User> usersByRole = this.userService.getUsersByRole(role);

                    if (usersByRole.isEmpty()) {
                        return new ResponseEntity<>("No users found for this role!", HttpStatus.NOT_FOUND);
                    }
                    Set<User> selectedUsers = new HashSet<>(usersByRole);
                    in.setUserSet(selectedUsers);
                    System.out.println(selectedUsers);
                } else {
                    return new ResponseEntity<>("Role group is empty!", HttpStatus.BAD_REQUEST);
                }

            }
            if (RecipientScope.ALL.getDisplayName().equals(recipientScope)) {
                Set<User> selectedUsers = new HashSet<>(this.userService.getUsers());
                in.setUserSet(selectedUsers);
                System.out.println(selectedUsers);
            }
            if (RecipientScope.INDIVIDUAL.getDisplayName().equals(recipientScope)) {
                if (request.containsKey("username") && !request.get("username").trim().isEmpty()) {
                    String username = request.get("username");
                    User user = this.userService.getUserByUsername(username);

                    if (user == null) {
                        return new ResponseEntity<>("User not found with username: " + username, HttpStatus.BAD_REQUEST);
                    }

                    Set<User> selectedUsers = new HashSet<>();
                    selectedUsers.add(user);
                    in.setUserSet(selectedUsers);

                    System.out.println("Individual selected user: " + selectedUsers);
                } else {
                    return new ResponseEntity<>("Email user is empty!", HttpStatus.BAD_REQUEST);
                }
            }
            in.setRecipientScope(recipientScope);
        } else {
            return new ResponseEntity<>("Recipient scope is empty!", HttpStatus.BAD_REQUEST);
        }

        this.invitationService.createOrUpdate(in);
        return new ResponseEntity<>("Invitation created!", HttpStatus.OK);
    }

    @DeleteMapping("/secure/invitation-post/{invitationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> deleteInvitationPost(@PathVariable(value = "invitationId") int id,
            Principal principal) {
        User currentUser = this.userService.getUserByUsername(principal.getName());
        Invitationpost invitation = this.invitationService.getInvitationPostById(id);

        if (invitation == null) {
            return new ResponseEntity("Cannot found post!", HttpStatus.NOT_FOUND);
        }

        if (Role.ROLE_ADMIN.getDisplayName().equals(currentUser.getRole())) {
            this.invitationService.deleteInvitationPost(id);
            return new ResponseEntity("Post deleted!", HttpStatus.OK);
        }
        return new ResponseEntity("You donnot have permission for this action", HttpStatus.FORBIDDEN);
    }

    //get list user join in this invitation
    @PutMapping(path = "/secure/invitation-post/{invitationId}")
    public ResponseEntity<String> updateInvitation(Principal principal,
            @RequestBody Map<String, String> request,
            @PathVariable(value = "invitationId") int id) {

        User u = this.userService.getUserByUsername(principal.getName());
        if (!Role.ROLE_ADMIN.getDisplayName().equals(u.getRole())) {
            return new ResponseEntity<>("Don’t have permission for this action", HttpStatus.FORBIDDEN);
        }

        Invitationpost in = this.invitationService.getInvitationPostById(id);
        Admin admin = this.adminService.getAdminById(u.getId());
        in.setAdminId(admin);

        if (request.containsKey("title") && !request.get("title").trim().isEmpty()) {
            in.setTitle(request.get("title"));
        } else {
            return new ResponseEntity<>("Title is empty!", HttpStatus.BAD_REQUEST);
        }

        if (request.containsKey("content") && !request.get("content").trim().isEmpty()) {
            in.setContent(request.get("content"));
        } else {
            return new ResponseEntity<>("Content is empty!", HttpStatus.BAD_REQUEST);
        }

        if (request.containsKey("recipientScope") && !request.get("recipientScope").trim().isEmpty()) {
            String recipientScope = request.get("recipientScope");

            //trên reactjs có thể chọn group user đẩy về [id, id2, id3, ...]
            if (RecipientScope.CUSTOM_GROUP.getDisplayName().equals(recipientScope)) {
                if (request.containsKey("user") && !request.get("user").trim().isEmpty()) {
                    String userIdsRaw = request.get("user");
                    List<Integer> userIds = Arrays.stream(userIdsRaw.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());
                    Set<User> selectedUsers = new HashSet<>();
                    for (Integer userId : userIds) {
                        User user = this.userService.getUserById(userId);
                        if (user != null) {
                            selectedUsers.add(user);
                        }
                    }
                    in.setUserSet(selectedUsers);
                    System.out.println(selectedUsers);
                } else {
                    return new ResponseEntity<>("User list is empty!", HttpStatus.BAD_REQUEST);
                }
            }

            if (RecipientScope.ROLE_GROUP.getDisplayName().equals(recipientScope)) {
                if (request.containsKey("role") && !request.get("role").trim().isEmpty()) {
                    String role = request.get("role");
                    System.out.println("role: " + role);
                    List<User> usersByRole = this.userService.getUsersByRole(role);

                    if (usersByRole.isEmpty()) {
                        return new ResponseEntity<>("No users found for this role!", HttpStatus.NOT_FOUND);
                    }
                    Set<User> selectedUsers = new HashSet<>(usersByRole);
                    in.setUserSet(selectedUsers);
                    System.out.println(selectedUsers);
                } else {
                    return new ResponseEntity<>("Role group is empty!", HttpStatus.BAD_REQUEST);
                }

            }
            if (RecipientScope.ALL.getDisplayName().equals(recipientScope)) {
                Set<User> selectedUsers = new HashSet<>(this.userService.getUsers());
                in.setUserSet(selectedUsers);
                System.out.println(selectedUsers);
            }
            if (RecipientScope.INDIVIDUAL.getDisplayName().equals(recipientScope)) {
                if (request.containsKey("username") && !request.get("username").trim().isEmpty()) {
                    String username = request.get("username");
                    User user = this.userService.getUserByUsername(username);

                    if (user == null) {
                        return new ResponseEntity<>("User not found with username: " + username, HttpStatus.BAD_REQUEST);
                    }

                    Set<User> selectedUsers = new HashSet<>();
                    selectedUsers.add(user);
                    in.setUserSet(selectedUsers);

                    System.out.println("Individual selected user: " + selectedUsers);
                } else {
                    return new ResponseEntity<>("Email user is empty!", HttpStatus.BAD_REQUEST);
                }
            }
            in.setRecipientScope(recipientScope);
        } else {
            return new ResponseEntity<>("Recipient scope is empty!", HttpStatus.BAD_REQUEST);
        }

        this.invitationService.createOrUpdate(in);
        return new ResponseEntity<>("Invitation updated!", HttpStatus.OK);
    }

    @GetMapping("/secure/invitation-post/") // ADMIN
    @ResponseBody
    @CrossOrigin
    public ResponseEntity<?> getInivitationPost(Principal principal, @RequestParam Map<String, String> params) {
        User currentUser = this.userService.getUserByUsername(principal.getName());
        if (Role.ROLE_ADMIN.getDisplayName().equals(currentUser.getRole())) {
            List<Invitationpost> posts = this.invitationService.getInvitationPosts(params);

            List<Map<String, Object>> result = posts.stream().map(post -> {
                Map<String, Object> postMap = new HashMap<>();
                postMap.put("id", post.getId());
                postMap.put("title", post.getTitle());
                postMap.put("content", post.getContent());
                postMap.put("recipientScope", post.getRecipientScope());
                postMap.put("createdAt", DateFormatter.format(post.getCreatedAt()));
                postMap.put("updatedAt", DateFormatter.format(post.getUpdateAt()));
                return postMap;
            }).collect(Collectors.toList());

            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>("You do not have permission for this action", HttpStatus.FORBIDDEN);
    }

}
