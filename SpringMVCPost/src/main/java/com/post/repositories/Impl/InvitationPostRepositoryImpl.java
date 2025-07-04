/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.repositories.Impl;

import com.post.pojo.Invitationpost;
import com.post.pojo.Invitationrecipient;
import com.post.pojo.User;
import com.post.repositories.InvitationPostRepository;

import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ASUS
 */
@Repository
@Transactional
@PropertySource("classpath:database.properties")
public class InvitationPostRepositoryImpl implements InvitationPostRepository {

    @Autowired
    public JavaMailSender mailSender;

    @Autowired
    private LocalSessionFactoryBean factory;
    @Autowired
    private Environment env;

    @Override
    public List<Invitationpost> getInvitationPosts(Map<String, String> params) {
        int PAGE_SIZE = Integer.parseInt(env.getProperty("PAGE_SIZE"));
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Invitationpost> q = b.createQuery(Invitationpost.class);
        Root<Invitationpost> root = q.from(Invitationpost.class);
        q.select(root);

        if (params != null) {

            List<Predicate> predicates = new ArrayList<>();

            // Tim kiếm theo người tạo
            String userId = params.get("userId");
            if (userId != null && !userId.isEmpty()) {
                predicates.add(b.equal(root.get("adminId").as(Integer.class), userId));
            }

            // 1. Tìm kiếm theo tên title
            String title = params.get("title");
            if (title != null && !title.isEmpty()) {
                predicates.add(b.like(root.get("title"), String.format("%%%s%%", title)));
            }

            // 2. Tìm kiếm theo content
            String content = params.get("content");
            if (content != null && !content.isEmpty()) {
                predicates.add(b.like(root.get("content"), String.format("%%%s%%", content)));
            }

            // 2. Tìm kiếm theo recipientScope
            String recipientScope = params.get("recipientScope");
            if (recipientScope != null && !recipientScope.isEmpty()) {
                predicates.add(b.like(root.get("recipientScope"), String.format("%%%s%%", recipientScope)));
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

            q.where(predicates.toArray(Predicate[]::new));

            // 4. Sắp xếp theo thời gian (gần nhất hoặc xa nhất)
            String order = params.get("order");
            if (order != null && !order.isEmpty()) {
                if (order.equalsIgnoreCase("desc")) {
                    q.orderBy(b.desc(root.get("createdAt")));
                } else {
                    q.orderBy(b.asc(root.get("createdAt")));
                }
            }
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
    public Invitationpost getInvitationPostById(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(Invitationpost.class, id);
    }

    @Override
    public Invitationpost createOrUpdate(Invitationpost sp) {
        Session s = this.factory.getObject().getCurrentSession();
          Set<User> users = sp.getUserSet();
            if (users != null && !users.isEmpty()) {
                for (User u : users) {
                    sendMail(
                            "phongkhamsaigoncare@gmail.com", // FROM
                            u.getUsername(), // TO
                            sp.getTitle(), // SUBJECT
                            sp.getContent() // BODY
                    );
                }
            }
        if (sp.getId() == null) {
            s.persist(sp);
        } else {
            s.merge(sp);
        }
        s.refresh(sp);

        return sp;
    }

    public void sendMail(String from, String to, String subject, String content) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(content);

        mailSender.send(mailMessage); // Gửi email
    }

    @Override
    public void deleteInvitationPost(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        Invitationpost c = s.get(Invitationpost.class, id);
        s.remove(c);
    }

    @Override
    public Map<String, Object> getInvitationRecipients(int invitationPostId, Map<String, String> params) {
        Set<User> users = this.getInvitationPostById(invitationPostId).getUserSet();
        int PAGE_SIZE = Integer.parseInt(env.getProperty("PAGE_SIZE"));
        List<Map<String, Object>> userList = users.stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("userId", user.getId());
                    userMap.put("username", user.getUsername());
                    return userMap;
                })
                .collect(Collectors.toList());

        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.get("page"));
            int start = (page - 1) * PAGE_SIZE;

            userList = userList.stream()
                    .skip(start)
                    .limit(PAGE_SIZE)
                    .collect(Collectors.toList());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("invitationId", invitationPostId);
        result.put("users", userList);

        return result;
    }
    //API 
    // CRUD trên invitationPost do chính admin quản lý
    //CRUD user tham gia invitationPost nếu primary(userId, invitationPostId) trùng lập thì thông báo

}
