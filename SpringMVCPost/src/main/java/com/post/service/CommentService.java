/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.post.service;

import com.post.pojo.Comment;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ASUS
 */
public interface CommentService {

    List<Comment> getComments(Map<String, String> params);

    Comment getCommentById(int id);

    Comment createOrUpdate(Comment c);

    void deleteComment(int id);

    long countCommentsByPostId(int postId);

    int getUserIdByCommentId(int commentId);
}
