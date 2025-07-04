/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hibernatepost;

import com.post.pojo.Admin;
import com.post.pojo.Alumni;
import com.post.pojo.Comment;
import com.post.pojo.InvitationPost;
import com.post.pojo.InvitationRecipient;
import com.post.pojo.Lecturer;
import com.post.pojo.Posts;
import com.post.pojo.Reaction;
import com.post.pojo.ResponseOption;
import com.post.pojo.SurveyOption;
import com.post.pojo.SurveyPost;
import com.post.pojo.SurveyQuestion;
import com.post.pojo.TimeOut;
import com.post.pojo.User;
import java.util.Properties;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;



/**
 *
 * @author ASUS
 */
public class HibernateUtils {
    private static final SessionFactory FACTORY;
    static {
        Configuration conf = new Configuration();
        Properties props = new Properties();
        props.put(Environment.DIALECT,"org.hibernate.dialect.MySQLDialect");
        props.put(Environment.JAKARTA_JDBC_DRIVER,"com.mysql.cj.jdbc.Driver");
        props.put(Environment.JAKARTA_JDBC_URL,"jdbc:mysql://localhost/postdb");
        props.put(Environment.JAKARTA_JDBC_USER,"root");
        props.put(Environment.JAKARTA_JDBC_PASSWORD,"12345");
        props.put(Environment.SHOW_SQL,"true");
        
        conf.setProperties(props);
        conf.addAnnotatedClass(User.class);
        conf.addAnnotatedClass(Alumni.class);
        conf.addAnnotatedClass(Admin.class);
        conf.addAnnotatedClass(Lecturer.class);
        conf.addAnnotatedClass(TimeOut.class);
        conf.addAnnotatedClass(Posts.class);
        conf.addAnnotatedClass(Comment.class);
        conf.addAnnotatedClass(Reaction.class);
        conf.addAnnotatedClass(SurveyPost.class);
        conf.addAnnotatedClass(SurveyQuestion.class);
        conf.addAnnotatedClass(SurveyOption.class);
        conf.addAnnotatedClass(ResponseOption.class);
        conf.addAnnotatedClass(InvitationPost.class);
        conf.addAnnotatedClass(InvitationRecipient.class);
        
        
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(conf.getProperties()).build();
        
        FACTORY = conf.buildSessionFactory(serviceRegistry);
    }

    /**
     * @return the FACTORY
     */
    public static SessionFactory getFACTORY() {
        return FACTORY;
    }
}
