/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.reponsitories.impl;

/**
 *
 * @author ASUS
 */
import com.hibernatepost.HibernateUtils;
import com.post.pojo.Alumni;
import jakarta.persistence.Query;
import java.util.List;
import org.hibernate.Session;

public class AlumniReponsitory {

    public List<Alumni> getAlumni() {
        try (Session s = HibernateUtils.getFACTORY().openSession()) {
            Query q = s.createQuery("FROM Alumni", Alumni.class);
            return q.getResultList();
        }
    }
}
