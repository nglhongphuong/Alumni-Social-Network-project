/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.repositories.Impl;

import com.post.pojo.Comment;
import com.post.pojo.User;
import com.post.repositories.CommentRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import org.hibernate.query.Query;

/**
 *
 * @author ASUS
 */
@Repository
@Transactional
@PropertySource("classpath:database.properties")
public class CommentRepositoryImpl implements CommentRepository {

    @Autowired
    private LocalSessionFactoryBean factory;
    @Autowired
    private Environment env;
    
    @Override
    public List<Comment> getComments(Map<String, String> params) {
        int PAGE_SIZE = Integer.parseInt(env.getProperty("PAGE_SIZE"));
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Comment> c = b.createQuery(Comment.class);
        Root root = c.from(Comment.class);
        c.select(root);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();

            String userName = params.get("username");
            if (userName != null && !userName.isEmpty()) {
                predicates.add(b.like(root.get("userId").get("username"), String.format("%%%s%%", userName)));
            }

            // 2. Tìm kiếm theo content
            String content = params.get("content");
            if (content != null && !content.isEmpty()) {
                predicates.add(b.like(root.get("content"), String.format("%%%s%%", content)));
            }

            String postId = params.get("postId");
            if (postId != null && !postId.isEmpty()) {
                predicates.add(b.equal(root.get("postId").as(Integer.class), postId));
            }

            // 3. Tìm kiếm theo thời gian (createdAt)
            String startDate = params.get("startDate");
            String endDate = params.get("endDate");

            if (startDate != null && !startDate.isEmpty()) {
                LocalDateTime start = LocalDateTime.parse(startDate);
                predicates.add(b.greaterThanOrEqualTo(root.get("createdAt"), start));
            }

            if (endDate != null && !endDate.isEmpty()) {
                LocalDateTime end = LocalDateTime.parse(endDate);
                predicates.add(b.lessThanOrEqualTo(root.get("createdAt"), end));
            }

            c.where(predicates.toArray(Predicate[]::new));

            String sort = params.get("sort");
            if (sort != null && !sort.isEmpty()) {
                if (sort.equalsIgnoreCase("desc")) {
                    c.orderBy(b.desc(root.get("createdAt")));
                } else {
                    c.orderBy(b.asc(root.get("createdAt")));
                }
            }
        }

        Query query = s.createQuery(c);

        if (params != null) {
            int page = Integer.parseInt(params.getOrDefault("page", "1"));
            int start = (page - 1) * PAGE_SIZE;
            query.setMaxResults(PAGE_SIZE);
            query.setFirstResult(start);
        }

        return query.getResultList();
    }

   
    @Override
    public Comment getCommentById (int id){
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(Comment.class, id);
    }
    
    @Override
    public Comment createOrUpdate(Comment c) {
       Session s = this.factory.getObject().getCurrentSession();
            if (c.getId() == null) 
                s.persist(c);
            else 
                s.merge(c);
            s.refresh(c);
        
        return c;
    }

    @Override
    public void deleteComment(int id) {
        Session s = this.factory.getObject().getCurrentSession();
            Comment c = s.get(Comment.class, id);
            s.remove(c);
        
    }
    
      @Override
    // đếm số lương comment trong 1 bài đăng
    public long countCommentsByPostId(int postId) {
        Session s = this.factory.getObject().getCurrentSession();
            CriteriaBuilder b = s.getCriteriaBuilder();
            CriteriaQuery<Long> c = b.createQuery(Long.class);
            Root<Comment> root = c.from(Comment.class);

            c.select(b.count(root));
            c.where(b.equal(root.get("postId").as(Integer.class), postId));

            Query query = s.createQuery(c);
            return (long) query.getSingleResult();
        
    }
    
    @Override
    public int getUserIdByCommentId(int commentId) {
        Session session = this.factory.getObject().getCurrentSession();
        Comment comId = session.get(Comment.class, commentId);
        User userId = comId.getUserId();
        return userId.getId();
    }
    

}
