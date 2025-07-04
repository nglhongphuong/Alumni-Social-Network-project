/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.reponsitories.impl;

import com.hibernatepost.HibernateUtils;
import com.post.pojo.Posts;
import jakarta.persistence.Query;
import java.util.List;
import org.hibernate.Session;

/**
 *
 * @author ASUS
 */
public class PostsReponsitory {
     public List<Posts> getPosts() {
        try (Session s = HibernateUtils.getFACTORY().openSession()) {
            Query q = s.createQuery("FROM Posts", Posts.class);
            return q.getResultList();
        }
     }
     public List<Object[]> getPostsWithReactionCounts() {
        try (Session session = HibernateUtils.getFACTORY().openSession()) {
            String hql = "SELECT p.id, "
                    + "SUM(CASE WHEN r.reactionType = 'LIKE' THEN 1 ELSE 0 END) AS likeCount, "
                    + "SUM(CASE WHEN r.reactionType = 'LOVE' THEN 1 ELSE 0 END) AS loveCount, "
                    + "SUM(CASE WHEN r.reactionType = 'ANGRY' THEN 1 ELSE 0 END) AS angryCount "
                    + "FROM Posts p "
                    + "LEFT JOIN p.reaction r "
                    + "GROUP BY p.id";

            var query = session.createQuery(hql, Object[].class);
            return query.getResultList();
        }
    }
}
