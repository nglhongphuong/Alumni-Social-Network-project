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
import com.post.pojo.Lecturer;
import jakarta.persistence.Query;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class LecturerReponsitory {

    public List<Lecturer> getLecturer() {
        try (Session s = HibernateUtils.getFACTORY().openSession()) {
            Query q = s.createQuery("FROM Lecturer", Lecturer.class);
            return q.getResultList();
        }
    }
    // Thêm giảng viên mới vào cơ sở dữ liệu với dữ liệu giả

    public void addLecturer(Lecturer lecturer) {
        Transaction transaction = null;
        try (Session session = HibernateUtils.getFACTORY().openSession()) {
            // Bắt đầu giao dịch
            transaction = session.beginTransaction();

            // Lưu đối tượng TimeOut trước
            if (lecturer.getTimeOut() != null) {
                session.saveOrUpdate(lecturer.getTimeOut());  // Lưu hoặc cập nhật TimeOut
            }

            // Lưu giảng viên vào cơ sở dữ liệu
            session.save(lecturer);

            // Cam kết giao dịch
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

}
