/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.controllers;

import com.post.enums.Role;
import com.post.pojo.Post;
import com.post.pojo.Reaction;
import com.post.pojo.ReactionPK;
import com.post.pojo.User;
import com.post.service.PostService;
import com.post.service.ReactionService;
import com.post.service.UserService;
import java.security.Principal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ASUS
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiReactionController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReactionService reactionService;

//Vua create vua cap nhap
    @PostMapping(path = "/secure/post/{postId}/reaction")
    public ResponseEntity<String> createReact(
            Principal principal,
            @RequestBody Map<String, String> request,
            @PathVariable(value = "postId") int id) {

        if (request == null || request.isEmpty()) {
            return new ResponseEntity<>("Dữ liệu gửi lên bị thiếu hoặc sai!", HttpStatus.BAD_REQUEST);
        }

        Post post = this.postService.getPostById(id);
        if (post == null) {
            return new ResponseEntity<>("Bài viết không tồn tại!", HttpStatus.NOT_FOUND);
        }

        User currentUser = this.userService.getUserByUsername(principal.getName());
        ReactionPK reactionPK = new ReactionPK(currentUser.getId(), post.getId());
        Reaction react = new Reaction(reactionPK);
        if (request.containsKey("reactType") && !request.get("reactType").trim().isEmpty()) {
            react.setReactionType(request.get("reactType"));
        } else {
            return new ResponseEntity<>("Bạn chưa chọn loại tương tác!", HttpStatus.BAD_REQUEST);
        }

        react.setPost(post);
        react.setUser(currentUser);

        this.reactionService.createOrUpdate(react);

        return new ResponseEntity<>("Tương tác đã được lưu!", HttpStatus.CREATED);
    }

    @DeleteMapping("/secure/post/{postId}/reaction")
    public ResponseEntity<String> deleteReaction(
            @PathVariable("postId") int id,
            Principal principal) {

        Post post = this.postService.getPostById(id);
        if (post == null) {
            return new ResponseEntity<>("Bài viết không tồn tại!", HttpStatus.NOT_FOUND);
        }

        User currentUser = this.userService.getUserByUsername(principal.getName());
        ReactionPK reactionPK = new ReactionPK(currentUser.getId(), post.getId());

        if (this.reactionService.getReactionById(reactionPK) == null) {
            return new ResponseEntity<>("Cannot delete reaction", HttpStatus.NOT_FOUND);
        }

        this.reactionService.deleteReaction(reactionPK);

        return new ResponseEntity<>("Tương tác đã được xóa!", HttpStatus.OK);
    }

    @GetMapping("/secure/post/{postId}/reaction")
    public ResponseEntity<?> getUserReaction(
            Principal principal,
            @PathVariable("postId") int postId) {

        User currentUser = userService.getUserByUsername(principal.getName());
        if (currentUser == null) {
            return new ResponseEntity<>("User không tồn tại", HttpStatus.UNAUTHORIZED);
        }

        String reactionType = reactionService.getReactionByUserAndPost(currentUser.getId(), postId);
        return ResponseEntity.ok(Map.of("reactionType", reactionType));
    }
}
