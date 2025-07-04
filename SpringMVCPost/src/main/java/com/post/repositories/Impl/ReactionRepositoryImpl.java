/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.repositories.Impl;

import com.post.pojo.Reaction;
import com.post.pojo.ReactionPK;
import com.post.pojo.User;
import com.post.repositories.ReactionRepository;
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
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ASUS
 */
@Repository
@Transactional
@PropertySource("classpath:database.properties")
public class ReactionRepositoryImpl implements ReactionRepository {

    @Autowired
    private LocalSessionFactoryBean factory;
    @Autowired
    private Environment env;

    @Override
    public List<Reaction> getReactions(Map<String, String> params) {
        int PAGE_SIZE = Integer.parseInt(env.getProperty("PAGE_SIZE"));
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Reaction> q = b.createQuery(Reaction.class);
        Root<Reaction> root = q.from(Reaction.class);
        q.select(root);

        if (params != null) {

            List<Predicate> predicates = new ArrayList<>();

            //Tìm kiếm theo userid
            String userId = params.get("userId");
            if (userId != null && !userId.isEmpty()) {
                predicates.add(b.equal(root.get("user").as(Integer.class), userId));
            }

            //Tìm kiếm theo postId
            String postId = params.get("postId");
            if (postId != null && !postId.isEmpty()) {
                predicates.add(b.equal(root.get("post").as(Integer.class), postId));
            }

            // 2. Tìm kiếm theo reactionType
            String reactionType = params.get("reactionType");
            if (reactionType != null && !reactionType.isEmpty()) {
                predicates.add(b.like(root.get("reactionType"), String.format("%%%s%%", reactionType)));
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
    public Reaction createOrUpdate(Reaction r) {
        Session s = this.factory.getObject().getCurrentSession();
        Reaction existingReaction = s.find(Reaction.class, r.getReactionPK());
        if (existingReaction == null) {
            s.persist(r);
            s.flush();
            System.out.println("Bạn đã thêm reaction mới!");

        } else {
            existingReaction.setReactionType(r.getReactionType());
            s.merge(existingReaction);
        }

        s.refresh(r);
        return r;
    }

    @Override
    public void deleteReaction(ReactionPK id) {
        Session s = this.factory.getObject().getCurrentSession();
        Reaction c = s.get(Reaction.class, id);
        s.remove(c);
    }

    @Override
    public Reaction getReactionById(ReactionPK id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(Reaction.class, id);
    }

    @Override
    public String getReactionByUserAndPost(int userId, int postId) {
        Session session = this.factory.getObject().getCurrentSession();
        ReactionPK reactionPK = new ReactionPK(userId, postId);
        Reaction react = this.getReactionById(reactionPK);
        if (react == null) {
        return "null";  
    }
        return react.getReactionType();
    }
    
     //Đếm số reaction trong 1 bài post
    @Override
    public long countByPostId(int postId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> c = b.createQuery(Long.class);
        Root<Reaction> root = c.from(Reaction.class);

        c.select(b.count(root));
        c.where(b.equal(root.get("post").as(Integer.class), postId));

        Query query = s.createQuery(c);
        return (long) query.getSingleResult();
    }
    
      @Override
    public List<Object[]> countByPostIdAndType(int postId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Object[]> c = b.createQuery(Object[].class);
        Root<Reaction> root = c.from(Reaction.class);

        c.multiselect(
                root.get("reactionType"),
                b.count(root)
        );

        Predicate postPredicate = b.equal(root.get("post").get("id"), postId);
        c.where(postPredicate);
        c.groupBy(root.get("reactionType"));
//

        Query query = s.createQuery(c);
        return query.getResultList();
    }

    @Override
    public int getUserIdByReactionPK(int userId, int postId) {
        ReactionPK pk = new ReactionPK();
        pk.setPostId(postId);
        pk.setUserId(userId);
        Session session = this.factory.getObject().getCurrentSession();
        Reaction react = session.get(Reaction.class, pk);

        return react.getUser().getId();

    }

    public int getUserId(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        Reaction rtId = session.get(Reaction.class, id);
        User userId = rtId.getUser();
        return userId.getId();

    }

    @Override
    public List<Object[]> getUsersByReactionType(int postId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Object[]> c = b.createQuery(Object[].class);
        Root<Reaction> root = c.from(Reaction.class);

        c.multiselect(
                root.get("reactionType"),
                root.get("user") // Đây là đối tượng User, đã ánh xạ @ManyToOne
        );

        c.where(b.equal(root.get("post").get("id"), postId));

        Query query = s.createQuery(c);
        return query.getResultList(); // row[0] = type, row[1] = User
    }
}
