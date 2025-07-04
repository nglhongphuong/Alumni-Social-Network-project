/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.service.impl;

import com.post.pojo.Comment;
import com.post.repositories.CommentRepository;
import com.post.service.CommentService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ASUS
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository comRepo;

    @Override
    public List<Comment> getComments(Map<String, String> params) {
        return this.comRepo.getComments(params);
    }

    @Override
    public Comment getCommentById(int id) {
        return this.comRepo.getCommentById(id);
    }

    @Override
    public Comment createOrUpdate(Comment c) {
        return this.comRepo.createOrUpdate(c);
    }

    @Override
    public void deleteComment(int id) {
        this.comRepo.deleteComment(id);
    }

    @Override
    public long countCommentsByPostId(int postId) {
        return this.comRepo.countCommentsByPostId(postId);
    }

    @Override
    public int getUserIdByCommentId(int commentId) {
        return this.comRepo.getUserIdByCommentId(commentId);
    }

}
