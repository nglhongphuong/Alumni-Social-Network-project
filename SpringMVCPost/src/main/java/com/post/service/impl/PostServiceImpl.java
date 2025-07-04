/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.post.pojo.Comment;
import com.post.pojo.Post;
import com.post.repositories.PostRepository;
import com.post.service.PostService;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ASUS
 */
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepo;

    @Autowired
    private Cloudinary cloudinary;

//    @Override
//    public List<Post> getPost() {
//        return this.postRepo.getPost();
//    }
    @Override
    public Post addOrUpdate(Post p) {
        if (p.getFile() != null && !p.getFile().isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(p.getFile().getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                p.setImage(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(PostServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return this.postRepo.addOrUpdate(p);

    }

    @Override
    public List<Post> getPosts(Map<String, String> params) {
        return this.postRepo.getPosts(params);
    }

    @Override
    public Post getPostById(int id) {
        return this.postRepo.getPostById(id);
    }

    @Override
    public void deleteProduct(int id) {
        this.postRepo.deleteProduct(id);
    }

    @Override
    public List<Comment> getComments(int postId) {
        return this.postRepo.getComments(postId);
    }

    @Override
    public List<Map<String, Object>> getListPost(Map<String, String> params) {
       return this.postRepo.getListPost(params);
        
    }
}
