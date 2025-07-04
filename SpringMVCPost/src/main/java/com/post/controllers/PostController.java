/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.controllers;

import com.post.enums.ReactionType;
import com.post.enums.Visibility;
import com.post.pojo.Comment;
import com.post.pojo.Post;
import com.post.pojo.Reaction;
import com.post.pojo.ReactionPK;
import com.post.pojo.User;
import com.post.service.CommentService;
import com.post.service.PostService;
import com.post.service.ReactionService;
import com.post.service.UserService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author ASUS
 */
@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService comService;

    @Autowired
    private ReactionService rtService;

    @Autowired
    private UserService userService;

    @RequestMapping("/post/")
    public String postView(Model model, @RequestParam Map<String, String> params) {
//        model.addAttribute("post", this.postService.getPosts(params));

        List<Post> posts = this.postService.getPosts(params);
        model.addAttribute("post", posts);

        // Đếm số comment cho từng post
        Map<Integer, Long> countComments = new HashMap<>();
        Map<Integer, Long> countReactions = new HashMap<>();
        for (Post p : posts) {
            long countCom = this.comService.countCommentsByPostId(p.getId());
            long countReact = this.rtService.countByPostId(p.getId());
            countComments.put(p.getId(), countCom);
            countReactions.put(p.getId(), countReact);

        }

        model.addAttribute("countComments", countComments);
        model.addAttribute("countReactions", countReactions);
        return "postView/post";
    }

    @GetMapping("/post/createPost")
    public String createView(Model model, @RequestParam Map<String, String> params) {
        model.addAttribute("user", this.userService.getUsers(params));
        model.addAttribute("visibility", Visibility.values());
        model.addAttribute("posts", new Post());
        return "postView/createPost";
    }

    @GetMapping("/post/createPost/{postId}")
    public String postView(Model model, @PathVariable(value = "postId") int id) {
        model.addAttribute("post", this.postService.getPostById(id));
        return "postView/createPost";
    }

    //    ================= ADD POST =================
    @PostMapping("/post/add-post")
    public String createPost(@ModelAttribute(value = "posts") Post p) {
        this.postService.addOrUpdate(p);
        return "redirect:/post/";
    }

    //    ================= UPDATE POST =================
    @GetMapping("/post/{postId}")
    public String updateView(Model model, @PathVariable(value = "postId") int id, @RequestParam Map<String, String> params) {
        model.addAttribute("visibility", Visibility.values());
        model.addAttribute("posts", this.postService.getPostById(id));
        model.addAttribute("user", this.userService.getUsers(params));
        return "postView/createPost";
    }

    //    ================= DELETE POST =================
    @GetMapping("/post/delete")
    public String deletePost(@RequestParam("id") int id) {

        this.postService.deleteProduct(id);
        return "redirect:/post/";
    }

    @RequestMapping("/post/{postId}/detail")
    public String postDetail(Model model, @PathVariable(value = "postId") int id, @RequestParam Map<String, String> params) {

        Post post = this.postService.getPostById(id);

        Map<String, String> postParams = new HashMap<>(params);
        postParams.put("postId", String.valueOf(post.getId()));

        // Đếm số lượng comment trong 1 bài post
        Map<Integer, Long> countComments = new HashMap<>();
        long countCom = this.comService.countCommentsByPostId(post.getId());
        countComments.put(post.getId(), countCom);

        //Đếm tất cả react trong 1 bài post
        Map<Integer, Long> countReactions = new HashMap<>();
        long countReact = this.rtService.countByPostId(post.getId());
        countReactions.put(post.getId(), countReact);

        //Đếm số lượng react theo Type
        Map<String, Long> countReactDt = new HashMap<>();
        List<Object[]> countReactDetail = this.rtService.countByPostIdAndType(id);
        for (Object[] row : countReactDetail) {
            String type = (String) row[0];
            Long count = (Long) row[1];
            countReactDt.put(type, count);
        }

        //lọc user theo reacType
        Map<String, List<User>> userReactionMap = new HashMap<>();
        List<Object[]> userReactType = this.rtService.getUsersByReactionType(id);
        for (Object[] row : userReactType) {
            String type = (String) row[0];
            User user = (User) row[1];
            userReactionMap.computeIfAbsent(type, k -> new ArrayList<>()).add(user);
        }

        model.addAttribute("post", post);
        model.addAttribute("comments", this.comService.getComments(postParams));
        model.addAttribute("reactions", this.rtService.getReactions(params));
        model.addAttribute("user", this.userService.getUsers(params));
        model.addAttribute("countComments", countComments);
        model.addAttribute("countAllReactions", countReactions);
        model.addAttribute("countReactDt", countReactDt);
        model.addAttribute("userReactType", userReactionMap);
        model.addAttribute("comment", new Comment());
        model.addAttribute("reactionType", ReactionType.values());
        model.addAttribute("reaction", new Reaction());
        return "postView/postDetail";
    }

//    ========================= ADD COMMENT ================
    @PostMapping("/post/{postId}/detail/add-comment")
    public String createComment(@PathVariable(value = "postId") int postId,
            @ModelAttribute(value = "comment") Comment c) {
        System.out.println("Step 3 : pass");
        System.out.println("comment c: " + c);

        Post post = this.postService.getPostById(postId); // Lấy đối tượng Post từ id
        c.setPostId(post); // Gán vào comment
        this.comService.createOrUpdate(c);

        return "redirect:/post/" + postId + "/detail";
    }

//    ======================= UPDATE COMMENT =====================
    @GetMapping("/post/{postId}/detail/{commentId}")
    public String updateComment(Model model,
            @PathVariable(value = "postId") int postId,
            @PathVariable(value = "commentId") int commentId,
            @RequestParam Map<String, String> params) {

        System.out.println("Step 1 : pass");
        Post post = this.postService.getPostById(postId);

        Map<String, String> postParams = new HashMap<>(params);
        postParams.put("postId", String.valueOf(post.getId()));

        // Đếm số lượng comment trong 1 bài post
        Map<Integer, Long> countComments = new HashMap<>();
        long countCom = this.comService.countCommentsByPostId(post.getId());
        countComments.put(post.getId(), countCom);

        //Đếm tất cả react trong 1 bài post
        Map<Integer, Long> countReactions = new HashMap<>();
        long countReact = this.rtService.countByPostId(post.getId());
        countReactions.put(post.getId(), countReact);

        //Đếm số lượng react theo Type
        Map<String, Long> countReactDt = new HashMap<>();
        List<Object[]> countReactDetail = this.rtService.countByPostIdAndType(postId);
        for (Object[] row : countReactDetail) {
            String type = (String) row[0];
            Long count = (Long) row[1];
            countReactDt.put(type, count);
        }

        //lọc user theo reacType
        Map<String, List<User>> userReactionMap = new HashMap<>();
        List<Object[]> userReactType = this.rtService.getUsersByReactionType(postId);
        for (Object[] row : userReactType) {
            String type = (String) row[0];
            User user = (User) row[1];
            userReactionMap.computeIfAbsent(type, k -> new ArrayList<>()).add(user);
        }
        System.out.println("Step 2 : pass");

        model.addAttribute("post", post);
        model.addAttribute("comments", this.comService.getComments(postParams));
        model.addAttribute("reactions", this.rtService.getReactions(params));

        model.addAttribute("user", this.userService.getUsers(params));
        model.addAttribute("countComments", countComments);
        model.addAttribute("countAllReactions", countReactions);
        model.addAttribute("countReactDt", countReactDt);
        model.addAttribute("userReactType", userReactionMap);
        model.addAttribute("comment", this.comService.getCommentById(commentId));
        model.addAttribute("reaction", new Reaction());
        return "postView/postDetail";
    }

    @GetMapping("/post/{postId}/detail/delete")
    public String deleteComment(@PathVariable(value = "postId") int postId, @RequestParam("id") int id) {

        this.comService.deleteComment(id);
        return "redirect:/post/" + postId + "/detail";
    }
//    ========================== ADD and update REACTION ==========================

    @PostMapping("/post/{postId}/detail/add-reaction")
    public String createUpdateReaction(@PathVariable(value = "postId") int postId,
            @ModelAttribute(value = "reaction") Reaction r,
            @RequestParam("userReaction") int userId) {
        System.out.println("posId: " + postId);
        System.out.println("userId: " + userId);
//      
        Post post = this.postService.getPostById(postId);
        User user = this.userService.getUserById(userId);

        ReactionPK pk = new ReactionPK(user.getId(), post.getId());

        r.setReactionPK(pk);
        r.setPost(post);
        r.setUser(user);
        this.rtService.createOrUpdate(r);

        return "redirect:/post/" + postId + "/detail";
    }

  @PostMapping("/post/{postId}/detail/delete-reaction")
    public String deleteReaction(@PathVariable(value = "postId") int postId,
            @ModelAttribute(value = "reaction") Reaction r,
            @RequestParam("userReaction") int userId) {
        System.out.println("posId: " + postId);
        System.out.println("userId: " + userId);
//      
        Post post = this.postService.getPostById(postId);
        User user = this.userService.getUserById(userId);

        ReactionPK pk = new ReactionPK(user.getId(), post.getId());
        
        Reaction react = this.rtService.getReactionById(pk);
        if(react != null){
            this.rtService.deleteReaction(pk);
            System.out.println("Sussessfull delete !!!");
        }
        System.out.println("Cannot find to delete !!!");
        

        return "redirect:/post/" + postId + "/detail";
    }

}
