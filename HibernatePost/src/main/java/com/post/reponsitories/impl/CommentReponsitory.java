/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.reponsitories.impl;

/**
 *
 * @author ASUS
 */

import com.hibernatepost.HibernateUtils;
import com.post.pojo.Comment;
import jakarta.persistence.Query;
import java.util.List;
import org.hibernate.Session;

public class CommentReponsitory {
     public List<Comment> getComment() {
        try (Session s = HibernateUtils.getFACTORY().openSession()) {
            Query q = s.createQuery("FROM Comment", Comment.class);
            return q.getResultList();
        }
    }
}
