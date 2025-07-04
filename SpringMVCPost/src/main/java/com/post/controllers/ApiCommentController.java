/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.controllers;

import com.post.enums.Role;
import com.post.pojo.Comment;
import com.post.pojo.Post;
import com.post.pojo.User;
import com.post.service.CommentService;
import com.post.service.PostService;
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
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ASUS
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiCommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @PostMapping(path = "/secure/post/{postId}/comment")
    public ResponseEntity<String> createComment(Principal principal,
            @RequestBody Map<String, String> request,
            @PathVariable(value = "postId") int id) {
        Post post = this.postService.getPostById(id);

        if (post == null) {
            return new ResponseEntity<>("Bài viết không tồn tại!", HttpStatus.NOT_FOUND);
        }

        Comment comment = new Comment();
        if (request.containsKey("content") && !request.get("content").trim().isEmpty()) {
            comment.setContent(request.get("content"));
        } else {
            return new ResponseEntity<>("Bạn chưa nhập nội dung bình luận!", HttpStatus.BAD_REQUEST);
        }
        comment.setPostId(post);
        comment.setUserId(this.userService.getUserByUsername(principal.getName()));
        this.commentService.createOrUpdate(comment);
        return new ResponseEntity("Bình luận đã được tạo", HttpStatus.OK);
    }

    @PutMapping("/secure/post/{postId}/comment/{commentId}")
    public ResponseEntity<String> updateComment(
            Principal principal,
            @RequestBody(required = true) Map<String, String> request,
            @PathVariable(value = "postId") int postId,
            @PathVariable(value = "commentId") int commentId) {

        if (request == null || request.isEmpty()) {
            return new ResponseEntity<>("Dữ liệu gửi lên bị thiếu hoặc sai định dạng!", HttpStatus.BAD_REQUEST);
        }

        User user = this.userService.getUserByUsername(principal.getName());
        Post post = this.postService.getPostById(postId);
        Comment comment = this.commentService.getCommentById(commentId);

        if (post == null) {
            return new ResponseEntity<>("Bài viết không tồn tại!", HttpStatus.NOT_FOUND);
        }
        if (comment == null) {
            return new ResponseEntity<>("Bình luận không tồn tại!", HttpStatus.NOT_FOUND);
        }

        System.out.println("User Role: " + user.getRole());
        System.out.println("Admin Role: " + Role.ROLE_ADMIN.getDisplayName());

        if (comment.getPostId().equals(post) && (comment.getUserId().equals(user) || post.getUserId().equals(user) || "ROLE_ADMIN".equals(user.getRole()))) {
            if (request.containsKey("content") && !request.get("content").trim().isEmpty()) {
                comment.setContent(request.get("content"));
            } else {
                return new ResponseEntity<>("Bạn chưa nhập nội dung bình luận!", HttpStatus.BAD_REQUEST);
            }

            this.commentService.createOrUpdate(comment);
            return new ResponseEntity<>("Bình luận đã được cập nhật!", HttpStatus.OK);
        }

        return new ResponseEntity<>("Bạn không được phép thực hiện hành động này!", HttpStatus.FORBIDDEN);
    }

    @DeleteMapping("/secure/post/{postId}/comment/{commentId}")
    public ResponseEntity<String> deleteComment(
            Principal principal,
            @PathVariable(value = "postId") int postId,
            @PathVariable(value = "commentId") int commentId) {

        User user = this.userService.getUserByUsername(principal.getName());
        Post post = this.postService.getPostById(postId);
        Comment comment = this.commentService.getCommentById(commentId);

        if (post == null) {
            return new ResponseEntity<>("Bài viết không tồn tại!", HttpStatus.NOT_FOUND);
        }
        if (comment == null) {
            return new ResponseEntity<>("Bình luận không tồn tại!", HttpStatus.NOT_FOUND);
        }

        System.out.println("User Role: " + user.getRole());
        System.out.println("Admin Role: " + Role.ROLE_ADMIN.getDisplayName());

        if ( (comment.getPostId().equals(post) && comment.getUserId().equals(user)) //Người bình luận của bài viết xóa comment
                || post.getUserId().equals(user) //Người dùng tạo bài viết xóa comment
                || "ROLE_ADMIN".equals(user.getRole()) //Admin có thể xóa comment
            ) 
        {
            this.commentService.deleteComment(commentId);
            return new ResponseEntity<>("Bình luận đã được xóa!", HttpStatus.OK);
        }

        return new ResponseEntity<>("Bạn không được phép thực hiện hành động này!", HttpStatus.FORBIDDEN);
    }
}
