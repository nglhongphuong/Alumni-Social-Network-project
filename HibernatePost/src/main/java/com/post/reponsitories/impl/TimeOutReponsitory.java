/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.reponsitories.impl;

import com.hibernatepost.HibernateUtils;
import com.post.pojo.TimeOut;
import jakarta.persistence.Query;
import java.util.List;
import org.hibernate.Session;

/**
 *
 * @author ASUS
 */
public class TimeOutReponsitory {
     public List<TimeOut> getTimeOut() {
        try (Session s = HibernateUtils.getFACTORY().openSession()) {
            Query q = s.createQuery("FROM TimeOut", TimeOut.class);
            return q.getResultList();
        }
     }
}
