/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.repositories.Impl;

import com.post.formatters.DateFormatter;
import com.post.pojo.Responseoption;
import com.post.pojo.Surveyoption;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.post.pojo.Surveypost;
import com.post.pojo.Surveyquestion;
import com.post.pojo.User;
import com.post.repositories.SurveyPostRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
public class SurveyPostRepositoryImpl implements SurveyPostRepository {

    @Autowired
    private LocalSessionFactoryBean factory;
    @Autowired
    private Environment env;

    @Override
    public List<Surveypost> getSurveyPost(Map<String, String> params) {
        int PAGE_SIZE = Integer.parseInt(env.getProperty("PAGE_SIZE"));
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Surveypost> sp = b.createQuery(Surveypost.class);
        Root root = sp.from(Surveypost.class);

        sp.select(root);

        if (params != null) {

            List<Predicate> predicates = new ArrayList<>();

            // 1. Tìm kiếm theo tên userId
            String userId = params.get("userId");
            if (userId != null && !userId.isEmpty()) {
                predicates.add(b.equal(root.get("userId").as(Integer.class), userId));
            }

            // 2. Tìm kiếm theo title
            String title = params.get("title");
            if (title != null && !title.isEmpty()) {
                predicates.add(b.like(root.get("title"), String.format("%%%s%%", title)));
            }

            // 2. Tìm kiếm theo content
            String description = params.get("description");
            if (description != null && !description.isEmpty()) {
                predicates.add(b.like(root.get("description"), String.format("%%%s%%", description)));
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

            sp.where(predicates.toArray(Predicate[]::new));

            // 4. Sắp xếp theo thời gian (gần nhất hoặc xa nhất)
            String order = params.get("order");
            if (order != null && !order.isEmpty()) {
                if (order.equalsIgnoreCase("desc")) {
                    sp.orderBy(b.desc(root.get("createdAt")));
                } else {
                    sp.orderBy(b.asc(root.get("createdAt")));
                }
            }
        }

        Query query = s.createQuery(sp);

        if (params != null) {
            int page = Integer.parseInt(params.getOrDefault("page", "1"));
            int start = (page - 1) * PAGE_SIZE;
            query.setMaxResults(PAGE_SIZE);
            query.setFirstResult(start);
        }

        return query.getResultList();
    }

    @Override
    public Surveypost getSurveyPostById(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(Surveypost.class, id);
    }

    @Override
    public Surveypost createOrUpdate(Surveypost sp) {
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
    public void deleteSurveyPost(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        Surveypost sp = s.get(Surveypost.class, id);
        s.remove(sp);

    }

    @Override
    public List<Surveyquestion> getQuestions(int surveyId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Surveyquestion> q = b.createQuery(Surveyquestion.class);
        Root root = q.from(Surveyquestion.class);
        q.select(root);

        q.where(b.equal(root.get("surveyId").as(Integer.class), surveyId));

        Query query = s.createQuery(q);
        return query.getResultList();
    }

    public List<Surveyoption> getOptions(int questionId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Surveyoption> q = b.createQuery(Surveyoption.class);
        Root root = q.from(Surveyoption.class);
        q.select(root);

        q.where(b.equal(root.get("questionId").as(Integer.class), questionId));

        org.hibernate.query.Query query = s.createQuery(q);
        return query.getResultList();
    }

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
    public List<Map<String, Object>> getListQuestion(int surveyId, Map<String, String> params) {
        int PAGE_SIZE = Integer.parseInt(env.getProperty("PAGE_SIZE"));
        int page = params.containsKey("page") ? Integer.parseInt(params.get("page")) : 1;
        int start = (page - 1) * PAGE_SIZE;

        List<Surveyquestion> questions = this.getQuestions(surveyId)
                .stream()
                .skip(start)
                .limit(PAGE_SIZE)
                .collect(Collectors.toList());

        List<Map<String, Object>> resultList = new ArrayList<>();

        for (Surveyquestion question : questions) {
            Map<String, Object> questionMap = new LinkedHashMap<>();

            questionMap.put("questionId", question.getId());
            questionMap.put("content", question.getContent());
            questionMap.put("responseType", question.getResponseType());
            questionMap.put("createdAt", DateFormatter.format(question.getCreatedAt()));
            questionMap.put("updatedAt", DateFormatter.format(question.getUpdateAt()));

            // Lấy danh sách option cho từng câu hỏi
            List<Surveyoption> options = getOptions(question.getId());
            List<Map<String, Object>> optionList = new ArrayList<>();

            if (options != null && !options.isEmpty()) {
                for (Surveyoption option : options) {
                    Map<String, Object> optionMap = new LinkedHashMap<>();
                    optionMap.put("optionId", option.getId());
                    optionMap.put("content", option.getContent());
                    optionMap.put("createdAt", DateFormatter.format(option.getCreatedAt()));
                    optionMap.put("updatedAt", DateFormatter.format(option.getUpdateAt()));
                    optionList.add(optionMap);
                }
            }
//DateFormatter.format(
            questionMap.put("options", optionList);
            questionMap.put("totalOptions", optionList.size());

            resultList.add(questionMap);
        }

        return resultList;
    }

    @Override
    public List<Map<String, Object>> getStats(int surveyId) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();

        // Lấy danh sách câu hỏi thuộc surveyId
        List<Surveyquestion> questions = getQuestions(surveyId);

        List<Map<String, Object>> statsList = new ArrayList<>();

        for (Surveyquestion question : questions) {
            // Lấy danh sách options cho từng câu hỏi
            List<Surveyoption> options = getOptions(question.getId());

            // Với mỗi option, đếm số user đã chọn
            List<Map<String, Object>> optionStats = new ArrayList<>();

            for (Surveyoption option : options) {
                // Đếm số user chọn option này
                CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
                Root<Responseoption> root = countQuery.from(Responseoption.class);
                countQuery.select(cb.count(root));
                countQuery.where(cb.equal(root.get("surveyoption").get("id"), option.getId()));

                Long voteCount = session.createQuery(countQuery).uniqueResult();

                Map<String, Object> optionMap = new LinkedHashMap<>();
                optionMap.put("optionId", option.getId());
                optionMap.put("optionContent", option.getContent());
                optionMap.put("voteCount", voteCount != null ? voteCount : 0L);

                optionStats.add(optionMap);
            }

            // Sắp xếp options theo số vote giảm dần
            optionStats.sort((o1, o2) -> Long.compare((Long) o2.get("voteCount"), (Long) o1.get("voteCount")));

            Map<String, Object> questionMap = new LinkedHashMap<>();
            questionMap.put("questionId", question.getId());
            questionMap.put("questionContent", question.getContent());
            questionMap.put("options", optionStats);

            statsList.add(questionMap);
        }

        return statsList;
    }

}
