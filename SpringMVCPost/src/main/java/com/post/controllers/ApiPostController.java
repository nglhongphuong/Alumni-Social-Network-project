/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.controllers;

import com.cloudinary.Cloudinary;
import com.post.enums.Role;
import com.post.pojo.Comment;
import com.post.pojo.Post;
import com.post.pojo.Reaction;
import com.post.pojo.ReactionPK;
import com.post.pojo.User;
import com.post.service.CommentService;
import com.post.service.PostService;
import com.post.service.ReactionService;
import com.post.service.UserService;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ASUS
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiPostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private Cloudinary cloudinary;

    @GetMapping("/secure/post/") //_ALL
    @ResponseBody
    @CrossOrigin
    public ResponseEntity<List<Map<String, Object>>> getPost(Principal principal, @RequestParam Map<String, String> params) {
        return new ResponseEntity<>(this.postService.getListPost(params), HttpStatus.OK);
    }

    @DeleteMapping("/secure/post/{postId}") //_ROLE_ADMIN and principal
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> deletePost(@PathVariable(value = "postId") int id, Principal principal) {
        User currentUser = this.userService.getUserByUsername(principal.getName());
        Post post = this.postService.getPostById(id);
        System.out.println("hahhahahah");
        if (post == null) {
            return new ResponseEntity("Cannot found post!", HttpStatus.NOT_FOUND);
        }

        if (post.getUserId().equals(currentUser) || Role.ROLE_ADMIN.getDisplayName().equals(currentUser.getRole())) {
            this.postService.deleteProduct(id);
            return new ResponseEntity("Bài viết đã được xóa!", HttpStatus.OK);
        }

        return new ResponseEntity("Bạn không có sự cho phép thực hiện hành động này", HttpStatus.FORBIDDEN);
    }

    @PutMapping(path = "/secure/post/{postId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updatePost(@PathVariable(value = "postId") int id,
            @RequestParam Map<String, String> params,
            @RequestParam(value = "file", required = false) MultipartFile file,
            Principal principal) {
        User currentUser = this.userService.getUserByUsername(principal.getName());
        Post post = this.postService.getPostById(id);
        if (post == null) {
            return new ResponseEntity("Cannot found post!", HttpStatus.NOT_FOUND);
        }
        
        System.out.println("Current User ID: " + currentUser.getId());
        System.out.println("Post User ID: " + post.getUserId().getId());

        if (post.getUserId().equals(currentUser) || Role.ROLE_ADMIN.getDisplayName().equals(currentUser.getRole())) {
            post.setFile(file);

            if (params.containsKey("content") && !params.get("content").trim().isEmpty()) {
                post.setContent(params.get("content"));
            }
            if (params.containsKey("isCommentLocked") && !params.get("isCommentLocked").trim().isEmpty()) {
                post.setIsCommentLocked(Boolean.valueOf(params.get("isCommentLocked")));
            }
            if (params.containsKey("visibility") && !params.get("visibility").trim().isEmpty()) {
                post.setVisibility(params.get("visibility"));
            }
            System.out.println("Bạn đã tới khúc này ....");
            this.postService.addOrUpdate(post);
            return new ResponseEntity("Bài viết đã được cập nhật!", HttpStatus.OK);
        }

        return new ResponseEntity("Bạn không có sự cho phép thực hiện hành động này", HttpStatus.FORBIDDEN);
    }

    @PostMapping(path = "/secure/post/")
    public ResponseEntity<String> createPost(
            @RequestParam Map<String, String> params,
            @RequestParam(value = "file", required = false) MultipartFile file,
            Principal principal) {
        User currentUser = this.userService.getUserByUsername(principal.getName());
        Post post = new Post();
        post.setUserId(currentUser);
        post.setFile(file);

        if (params.containsKey("content") && !params.get("content").trim().isEmpty()) {
            post.setContent(params.get("content"));
        }
        if (params.containsKey("isCommentLocked") && !params.get("isCommentLocked").trim().isEmpty()) {
            post.setIsCommentLocked(Boolean.valueOf(params.get("isCommentLocked")));
        }
        if (params.containsKey("visibility") && !params.get("visibility").trim().isEmpty()) {
            post.setVisibility(params.get("visibility"));
        }
        System.out.println("Bạn đã tới khúc này ....");
        this.postService.addOrUpdate(post);
        return new ResponseEntity("Bài viết đã được tạo", HttpStatus.OK);
    }

}
