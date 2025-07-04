/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.service.impl;

import com.post.repositories.ReactionRepository;
import com.post.service.ReactionService;
import java.util.List;
import java.util.Map;
import com.post.pojo.Reaction;
import com.post.pojo.ReactionPK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ASUS
 */
@Service
public class ReactionServiceImpl implements ReactionService {

    @Autowired
    private ReactionRepository reactRepo;

    @Override
    public List<Reaction> getReactions(Map<String, String> params) {
        return this.reactRepo.getReactions(params);
    }

    @Override
    public Reaction createOrUpdate(Reaction r) {
        return this.reactRepo.createOrUpdate(r);
    }

    @Override
    public void deleteReaction(ReactionPK id) {
        this.reactRepo.deleteReaction(id);
    }
    
    @Override
    public Reaction getReactionById(ReactionPK id)
    {
        return this.reactRepo.getReactionById(id);
    }

    @Override
    public String getReactionByUserAndPost(int userId, int postId) {
        return this.reactRepo.getReactionByUserAndPost(userId, postId);
    }

    @Override
    public List<Object[]> countByPostIdAndType(int postId) {
        return this.reactRepo.countByPostIdAndType(postId);
    }

    @Override
    public int getUserIdByReactionPK(int userId, int postId) {
        return this.reactRepo.getUserIdByReactionPK(userId, postId);
    }

    @Override
    public int getUserId(int id) {
        return this.reactRepo.getUserId(id);
    }

    @Override
    public List<Object[]> getUsersByReactionType(int postId) {
        return this.reactRepo.getUsersByReactionType(postId);
    }

    @Override
    public long countByPostId(int postId) {
        return this.reactRepo.countByPostId(postId);
    }

}
