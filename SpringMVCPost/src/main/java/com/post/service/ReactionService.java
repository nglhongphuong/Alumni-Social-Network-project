/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.post.service;

import com.post.pojo.Reaction;
import com.post.pojo.ReactionPK;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ASUS
 */
public interface ReactionService {

    List<Reaction> getReactions(Map<String, String> params);

    Reaction createOrUpdate(Reaction r);

    void deleteReaction(ReactionPK id);
    
    Reaction getReactionById(ReactionPK id);
    
    String getReactionByUserAndPost(int userId, int postId);
    
      // Đếm số reaction theo kiểu (like, love...) và post
    List<Object[]> countByPostIdAndType(int postId);

    int getUserIdByReactionPK(int userId, int postId);

    int getUserId(int id);

    List<Object[]> getUsersByReactionType(int postId);
    
    long countByPostId(int postId);
}
