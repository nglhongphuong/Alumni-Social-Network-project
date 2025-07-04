/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.repositories.Impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import com.post.pojo.Admin;
import com.post.pojo.Comment;
import com.post.pojo.Invitationpost;
import com.post.pojo.Surveypost;
import com.post.pojo.User;
import com.post.repositories.AdminRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ASUS
 */
@Repository
@Transactional
@PropertySource("classpath:database.properties")
public class AdminRepositoryImpl implements AdminRepository{
    
    @Autowired
    private LocalSessionFactoryBean factory;
    @Autowired
    private Environment env;
    
     @Override
    public List<Admin> getAdmins(Map<String, String> params) {
         int PAGE_SIZE = Integer.parseInt(env.getProperty("PAGE_SIZE"));
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Admin> c = b.createQuery(Admin.class);
        Root root = c.from(Admin.class);
        c.select(root);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();

            //Tìm kiếm theo userid
            String userId = params.get("userId");
            if (userId != null && !userId.isEmpty())
                predicates.add(b.equal(root.get("userId").as(Integer.class), userId));
            
            
            
            c.where(predicates.toArray(Predicate[]::new));
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
    public Admin getAdminById (int id){
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(Admin.class, id);
    }
    
   
    @Override
    public void deleteAdmin(int id) {
        Session s = this.factory.getObject().getCurrentSession();
            Admin c = s.get(Admin.class, id);
            s.remove(c);
        
    }
    
    @Override
    public List<Surveypost> getSurveyposts(int userId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Surveypost> q = b.createQuery(Surveypost.class);
        Root root = q.from(Surveypost.class);
        q.select(root);
        
        q.where(b.equal(root.get("userId").as(Integer.class), userId));
        
        Query query = s.createQuery(q);
        return query.getResultList();
    }
    
     @Override
    public List<Invitationpost> getInvitationposts(int userId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Invitationpost> q = b.createQuery(Invitationpost.class);
        Root root = q.from(Invitationpost.class);
        q.select(root);
        
        q.where(b.equal(root.get("userId").as(Integer.class), userId));
        
        Query query = s.createQuery(q);
        return query.getResultList();
    }
    
    
}
