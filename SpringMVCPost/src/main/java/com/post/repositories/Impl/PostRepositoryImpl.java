/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.repositories.Impl;

import com.post.enums.ReactionType;
import com.post.formatters.DateFormatter;
import com.post.pojo.Comment;
import com.post.pojo.Post;
import com.post.pojo.Reaction;
import com.post.pojo.User;
import com.post.repositories.PostRepository;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
public class PostRepositoryImpl implements PostRepository {

    @Autowired
    private LocalSessionFactoryBean factory;
    private static final int PAGE_SIZE = 6;

    @Override
//    public List<Post> getPost(){
//        Session s = this.factory.getObject().getCurrentSession();
//        Query q = s.createQuery("FROM Post", Post.class);
//        return q.getResultList();
//    }
    public List<Post> getPosts(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Post> q = b.createQuery(Post.class);
        Root<Post> root = q.from(Post.class);
        q.select(root);

        if (params != null) {

            List<Predicate> predicates = new ArrayList<>();

            String postIdStr = params.get("postId");
            if (postIdStr != null && !postIdStr.isEmpty()) {
                int postId = Integer.parseInt(postIdStr);
                predicates.add(b.equal(root.get("id"), postId));
            }

            // 🔍 Tìm kiếm theo `userId`
            String userIdStr = params.get("userId");
            if (userIdStr != null && !userIdStr.isEmpty()) {
                int userId = Integer.parseInt(userIdStr);
                predicates.add(b.equal(root.get("userId").get("id"), userId));
            }

            // 1. Tìm kiếm theo tên username
            String userName = params.get("username");
            if (userName != null && !userName.isEmpty()) {
                predicates.add(b.like(root.get("userId").get("username"), String.format("%%%s%%", userName)));
            }

            // 2. Tìm kiếm theo content
            String content = params.get("content");
            if (content != null && !content.isEmpty()) {
                predicates.add(b.like(root.get("content"), String.format("%%%s%%", content)));
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
    public Post addOrUpdate(Post p) {
        Session s = this.factory.getObject().getCurrentSession();
        if (p.getId() == null) {

            s.persist(p);
        } else {
            s.merge(p);
        }
        s.refresh(p);
        return p;
    }

    @Override
    public Post getPostById(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(Post.class, id);
    }

    @Override
    public void deleteProduct(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        Post p = this.getPostById(id);
        s.remove(p);
    }

    @Override
    public List<Comment> getComments(int postId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Comment> q = b.createQuery(Comment.class);
        Root root = q.from(Comment.class);
        q.select(root);

        q.where(b.equal(root.get("postId").as(Integer.class), postId));

        Query query = s.createQuery(q);
        return query.getResultList();
    }

    public List<Map<String, Object>> getReactions(int postId) {
        Session session = this.factory.getObject().getCurrentSession();

        String hql = "SELECT r.reactionType, COUNT(r.reactionType) "
                + "FROM Reaction r WHERE r.post.id = :postId "
                + "GROUP BY r.reactionType";

        var query = session.createQuery(hql, Object[].class);
        query.setParameter("postId", postId);

        List<Object[]> results = query.getResultList();
        Map<String, Object> reactionSummary = new LinkedHashMap<>();

        // Tổng tất cả các loại phản ứng
        reactionSummary.put("total", results.stream().mapToLong(row -> (long) row[1]).sum());

        for (Object[] row : results) {
            String type = (String) row[0];
            long count = (long) row[1];

            reactionSummary.put(type.toLowerCase(), count);
        }

        return List.of(reactionSummary);
    }

    /**
     *
     * @param params
     * @return
     */
    @Override
    public List<Map<String, Object>> getListPost(Map<String, String> params) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        List<Post> posts = getPosts(params);
        List<Map<String, Object>> resultList = new ArrayList<>();

        for (Post post : posts) {
            Map<String, Object> postMap = new LinkedHashMap<>();

            postMap.put("postId", post.getId());
            postMap.put("username", post.getUserId() != null ? post.getUserId().getUsername() : "Unknown");
            postMap.put("name", post.getUserId().getName());
            postMap.put("userId", post.getUserId().getId());
            postMap.put("avatar", post.getUserId().getAvatar());
            postMap.put("visibility", post.getVisibility());
            postMap.put("content", post.getContent());
            postMap.put("image", post.getImage());
            postMap.put("isCommentLocked", post.getIsCommentLocked());

            postMap.put("createdAt", DateFormatter.format(post.getCreatedAt()));
            postMap.put("updatedAt", DateFormatter.format(post.getUpdatedAt()));

            System.out.println("Fetching reactions for post: " + post.getId());
            List<Map<String, Object>> reactions = getReactions(post.getId());
            System.out.println("Reactions result: " + reactions);

            postMap.put("reactions", reactions);

            System.out.println("Fetching comments for post: " + post.getId());
            List<Comment> comments = getComments(post.getId());
            System.out.println("Comments result: " + comments);

            List<Map<String, Object>> commentList = new ArrayList<>();
            if (comments != null && !comments.isEmpty()) {
                for (Comment comment : comments) {
                    Map<String, Object> commentMap = new LinkedHashMap<>();
                    commentMap.put("commentId", comment.getId());
                    commentMap.put("user", comment.getUserId() != null ? comment.getUserId().getUsername() : "Unknown");
                    commentMap.put("name", comment.getUserId().getName());
                    commentMap.put("avatar", comment.getUserId().getAvatar());
                    commentMap.put("content", comment.getContent());
                    commentMap.put("createdAt", DateFormatter.format(comment.getCreatedAt()));
                    commentList.add(commentMap);
                }
            }
            postMap.put("comments", commentList);
            postMap.put("totalComments", commentList.size());

            resultList.add(postMap);
        }

        return resultList;
    }

}
