/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.post.repositories;

import com.post.pojo.Comment;
import com.post.pojo.Post;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ASUS
 */
public interface PostRepository  {
//    List<Post> getPost();
    List<Post> getPosts(Map<String, String> params);
    List<Map<String, Object>> getListPost(Map<String, String> params);
    Post addOrUpdate(Post p);
    Post getPostById(int id);
    void deleteProduct(int id);
    List<Comment> getComments(int postId);
}
