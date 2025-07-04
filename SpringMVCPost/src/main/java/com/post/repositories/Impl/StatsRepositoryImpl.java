/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.repositories.Impl;

import com.post.repositories.StatsRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ASUS
 */
@Repository
@Transactional
@PropertySource("classpath:database.properties")
public class StatsRepositoryImpl implements StatsRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Map<String, Object>> getUserStats() {
        Session session = factory.getObject().getCurrentSession();
        String hql = "SELECT year(u.createdAt) as year, month(u.createdAt) as month, count(u.id) as total "
                + "FROM User u "
                + "GROUP BY year(u.createdAt), month(u.createdAt) "
                + "ORDER BY year(u.createdAt), month(u.createdAt)";

        List<Object[]> results = session.createQuery(hql).getResultList();

        List<Map<String, Object>> stats = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("year", row[0]);
            map.put("month", row[1]);
            map.put("totalUsers", row[2]);
            stats.add(map);
        }
        return stats;
    }

    @Override
    public List<Map<String, Object>> getPostStats() {
        Session session = factory.getObject().getCurrentSession();
        String hql = "SELECT year(p.createdAt) as year, month(p.createdAt) as month, count(p.id) as total "
                + "FROM Post p "
                + "GROUP BY year(p.createdAt), month(p.createdAt) "
                + "ORDER BY year(p.createdAt), month(p.createdAt)";

        List<Object[]> results = session.createQuery(hql).getResultList();

        List<Map<String, Object>> stats = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("year", row[0]);
            map.put("month", row[1]);
            map.put("totalPosts", row[2]);
            stats.add(map);
        }
        return stats;
    }

    @Override
    public List<Map<String, Object>> getSurveyPostStats() {
        Session session = factory.getObject().getCurrentSession();
        String hql = "SELECT year(sp.createdAt) as year, month(sp.createdAt) as month, count(sp.id) as total "
                + "FROM Surveypost sp "
                + "GROUP BY year(sp.createdAt), month(sp.createdAt) "
                + "ORDER BY year(sp.createdAt), month(sp.createdAt)";

        List<Object[]> results = session.createQuery(hql).getResultList();

        List<Map<String, Object>> stats = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("year", row[0]);
            map.put("month", row[1]);
            map.put("totalSurveyPosts", row[2]);
            stats.add(map);
        }
        return stats;
    }

    @Override
    public List<Map<String, Object>> getInvitationPostStats() {
        Session session = factory.getObject().getCurrentSession();
        String hql = "SELECT year(ip.createdAt) as year, month(ip.createdAt) as month, count(ip.id) as total "
                + "FROM Invitationpost ip "
                + "GROUP BY year(ip.createdAt), month(ip.createdAt) "
                + "ORDER BY year(ip.createdAt), month(ip.createdAt)";

        List<Object[]> results = session.createQuery(hql).getResultList();

        List<Map<String, Object>> stats = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("year", row[0]);
            map.put("month", row[1]);
            map.put("totalInvitationPosts", row[2]);
            stats.add(map);
        }
        return stats;
    }
}
