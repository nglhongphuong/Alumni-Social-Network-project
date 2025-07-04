/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.reponsitories.impl;

import com.hibernatepost.HibernateUtils;
import com.post.pojo.SurveyPost;
import jakarta.persistence.Query;
import java.util.List;
import org.hibernate.Session;

/**
 *
 * @author ASUS
 */
public class SurveyPostReponsitory {

    public List<SurveyPost> getSurveyPost() {
        try (Session s = HibernateUtils.getFACTORY().openSession()) {
            Query q = s.createQuery("FROM SurveyPost", SurveyPost.class);
            return q.getResultList();
        }
    }

    public List<SurveyPost> getSurveyPostWithResponses() {
        try (Session s = HibernateUtils.getFACTORY().openSession()) {
            String hql = """
            SELECT DISTINCT sp
            FROM SurveyPost sp
            LEFT JOIN FETCH sp.surveyQuestion q
            LEFT JOIN FETCH q.surveyOption o
            LEFT JOIN FETCH o.responseOption ro
            """;
            Query  q = s.createQuery(hql, SurveyPost.class);
            return q.getResultList();
        }
    }

}
