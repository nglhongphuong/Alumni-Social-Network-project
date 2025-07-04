/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.repositories.Impl;

import com.post.pojo.Surveyoption;
import com.post.pojo.Surveyquestion;
import com.post.repositories.SurveyQuestionRepository;
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
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import org.hibernate.query.Query;

/**
 *
 * @author ASUS
 */
@Repository
@Transactional
@PropertySource("classpath:database.properties")
public class SurveyQuestionRepositoryImpl implements SurveyQuestionRepository {

    @Autowired
    private LocalSessionFactoryBean factory;
    @Autowired
    private Environment env;

    @Override
    public List<Surveyquestion> getQuestion(Map<String, String> params) {
        int PAGE_SIZE = Integer.parseInt(env.getProperty("PAGE_SIZE"));
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Surveyquestion> c = b.createQuery(Surveyquestion.class);
        Root root = c.from(Surveyquestion.class);
        c.select(root);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();

            String content = params.get("content");
            if (content != null && !content.isEmpty()) {
                predicates.add(b.like(root.get("content"), String.format("%%%s%%", content)));
            }

            String surveyId = params.get("surveyId");
            if (surveyId != null && !surveyId.isEmpty()) {
                predicates.add(b.equal(root.get("surveyId").as(Integer.class), surveyId));
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

            String order = params.get("order");
            if (order != null && !order.isEmpty()) {
                if (order.equalsIgnoreCase("desc")) {
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
    public Surveyquestion getSurveyQuestionById(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(Surveyquestion.class, id);
    }

    @Override
    public Surveyquestion createOrUpdate(Surveyquestion sp) {
        Session s = this.factory.getObject().getCurrentSession();
        if (sp.getId() == null) {
            s.persist(sp);
        } else {
            s.merge(sp);
        }

        s.refresh(sp);

        return sp;
    }

    @Override
    public void deleteSurveyQuestion(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        Surveyquestion c = s.get(Surveyquestion.class, id);
        s.remove(c);

    }

    @Override
    public List<Surveyoption> getOptions(int questionId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Surveyoption> q = b.createQuery(Surveyoption.class);
        Root root = q.from(Surveyoption.class);
        q.select(root);

        q.where(b.equal(root.get("questionId").as(Integer.class), questionId));

        Query query = s.createQuery(q);
        return query.getResultList();
    }
    
     @Override
    public long countSurveyOptionsBySurveyId(int surveyId) {
        Session s = this.factory.getObject().getCurrentSession();
            CriteriaBuilder b = s.getCriteriaBuilder();
            CriteriaQuery<Long> c = b.createQuery(Long.class);
            Root<Surveyquestion> root = c.from(Surveyquestion.class);

            c.select(b.count(root));
            c.where(b.equal(root.get("surveyId").as(Integer.class), surveyId));

            jakarta.persistence.Query query = s.createQuery(c);
            return (long) query.getSingleResult(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
