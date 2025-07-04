/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.reponsitories.impl;

import com.hibernatepost.HibernateUtils;
import com.post.pojo.SurveyOption;
import jakarta.persistence.Query;
import java.util.List;
import org.hibernate.Session;

/**
 *
 * @author ASUS
 */
public class SurveyOptionReponsitory {
            public List<SurveyOption> getSurveyOption() {
        try (Session s = HibernateUtils.getFACTORY().openSession()) {
            Query q = s.createQuery("FROM SurveyOption", SurveyOption.class);
            return q.getResultList();
        }
    }
}
