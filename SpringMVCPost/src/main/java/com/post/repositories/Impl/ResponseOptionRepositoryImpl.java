/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.repositories.Impl;

import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import com.post.pojo.Responseoption;
import com.post.pojo.Surveyoption;
import com.post.pojo.ResponseoptionPK;
import com.post.pojo.User;
import com.post.repositories.ResponseOptionRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
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
public class ResponseOptionRepositoryImpl implements ResponseOptionRepository {

    @Autowired
    private LocalSessionFactoryBean factory;
    @Autowired
    private Environment env;

    @Override
    public List<Responseoption> getResponse(Map<String, String> params) {
        int PAGE_SIZE = Integer.parseInt(env.getProperty("PAGE_SIZE"));
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Responseoption> q = b.createQuery(Responseoption.class);
        Root<Responseoption> root = q.from(Responseoption.class);
        q.select(root);

        if (params != null) {

            List<Predicate> predicates = new ArrayList<>();

            //Tìm kiếm theo userid
            String userId = params.get("userId");
            if (userId != null && !userId.isEmpty()) {
                predicates.add(b.equal(root.get("user").as(Integer.class), userId));
            }

            //Tìm kiếm theo option_id
            String surveyoption = params.get("surveyoption");
            if (surveyoption != null && !surveyoption.isEmpty()) {
                predicates.add(b.equal(root.get("surveyoption").as(Integer.class), surveyoption));
            }

            // 3. Tìm kiếm theo thời gian (createdAt)
            String startDate = params.get("startDate");
            String endDate = params.get("endDate");

            if (startDate != null && !startDate.isEmpty()) {
                LocalDateTime start = LocalDateTime.parse(startDate);
                predicates.add(b.greaterThanOrEqualTo(root.get("respondedAt"), start));
            }

            if (endDate != null && !endDate.isEmpty()) {
                LocalDateTime end = LocalDateTime.parse(endDate);
                predicates.add(b.lessThanOrEqualTo(root.get("respondedAt"), end));
            }

            q.where(predicates.toArray(Predicate[]::new));

//            // 4. Sắp xếp theo thời gian (gần nhất hoặc xa nhất)
//            String order = params.get("order");
//            if (order != null && !order.isEmpty()) {
//                if (order.equalsIgnoreCase("desc")) {
//                    q.orderBy(b.desc(root.get("respondedAt")));
//                } else {
//                    q.orderBy(b.asc(root.get("respondedAt")));
//                }
//            }
        }

        // Phân trang
        Query query = s.createQuery(q);
        if (params == null || !params.containsKey("page")) {
        } else {
            int page = Integer.parseInt(params.get("page"));
            int start = (page - 1) * PAGE_SIZE;

            query.setMaxResults(PAGE_SIZE);
            query.setFirstResult(start);
        }
        return query.getResultList();
    }

    @Override
    public Responseoption createOrUpdate(Responseoption r) {
        Session s = this.factory.getObject().getCurrentSession();
        Responseoption existingResponse = s.find(Responseoption.class, r.getResponseoptionPK());
        if (existingResponse == null) {
            s.persist(r);
            s.flush();
        } else {
            existingResponse.setSurveyoption(r.getSurveyoption());
            s.merge(existingResponse);
        }
        s.refresh(r);
        return r;
    }

    @Override
    public void deleteResponseoption(ResponseoptionPK id) {
        Session s = this.factory.getObject().getCurrentSession();
        Responseoption c = s.get(Responseoption.class, id);
        s.remove(c);
    }

    @Override
    public Responseoption getResponseoptionId(ResponseoptionPK id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(Responseoption.class, id);
    }

    @Override
    public List<User> getUserResponse(int optionId) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<Responseoption> root = cq.from(Responseoption.class);

        // Lấy user từ Responseoption
        cq.select(root.get("user"));
        // Lọc theo optionId
        cq.where(cb.equal(root.get("surveyoption").get("id"), optionId));

        // Loại bỏ user trùng lặp (distinct)
        cq.distinct(true);

        var query = session.createQuery(cq);
        return query.getResultList();
    }
    
      @Override
    public List<Surveyoption> getOptionsByUserId(int userId) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Surveyoption> cq = cb.createQuery(Surveyoption.class);
        Root<Responseoption> root = cq.from(Responseoption.class);

        // Lấy surveyoption từ Responseoption
        cq.select(root.get("surveyoption"));
        // Lọc theo userId
        cq.where(cb.equal(root.get("user").get("id"), userId));

        // Loại bỏ user trùng lặp (distinct)
        cq.distinct(true);

        var query = session.createQuery(cq);
        return query.getResultList();
    }
    
    
    
    @Override
    public List<Responseoption> getResponsesByUserAndSurvey(int userId, int surveyId) {
        Session session = this.factory.getObject().getCurrentSession();

        String hql = "SELECT r FROM Responseoption r " +
                     "WHERE r.responseoptionPK.userId = :userId " +
                     "AND r.surveyoption.questionId.surveyPost.id = :surveyId";

        Query  query = session.createQuery(hql, Responseoption.class);
        query.setParameter("userId", userId);
        query.setParameter("surveyId", surveyId);

        return query.getResultList();
    }

}
