/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.repositories.Impl;

import com.post.enums.Role;
import com.post.pojo.Admin;
import com.post.pojo.Alumni;
import com.post.pojo.Comment;
import com.post.pojo.Reaction;
import com.post.pojo.Lecturer;
import com.post.pojo.Post;
import com.post.pojo.Responseoption;
import com.post.pojo.Timeout;
import com.post.pojo.User;
import java.util.HashMap;
import com.post.repositories.UserRepository;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ASUS
 */
@Repository
@Transactional
@PropertySource("classpath:database.properties")
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    public JavaMailSender mailSender;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private Environment env;

    @Override
    public List<User> getUsers() {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createQuery("FROM User", User.class);
        return q.getResultList();
    }

    @Override
    public List<User> getUsers(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.select(root);

        if (params != null) {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Lọc theo username (LIKE)
            String username = params.get("username");
            if (username != null && !username.isEmpty()) {
                predicates.add(builder.like(root.get("username"), "%" + username + "%"));
            }

            // 2. Lọc theo role (EQUALS)
            String role = params.get("role");
            if (role != null && !role.isEmpty()) {
                predicates.add(builder.equal(root.get("role"), role));
            }

            // 3. Lọc theo khoảng thời gian createdAt
            String startDate = params.get("startDate");
            String endDate = params.get("endDate");

            if (startDate != null && !startDate.isEmpty()) {
                LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
                predicates.add(builder.greaterThanOrEqualTo(root.get("createdAt"), start));
            }

            if (endDate != null && !endDate.isEmpty()) {
                LocalDateTime end = LocalDate.parse(endDate).atTime(LocalTime.MAX);
                predicates.add(builder.lessThanOrEqualTo(root.get("createdAt"), end));
            }

            //Lọc theo trạng thái tài khoản (isActive)**
            String isActive = params.get("isActive");
            if (isActive != null && !isActive.isEmpty()) {
                boolean activeStatus = Boolean.parseBoolean(isActive);
                predicates.add(builder.equal(root.get("isActive"), activeStatus));
            }

            // Thêm điều kiện WHERE
            query.where(predicates.toArray(Predicate[]::new));

            // 4. Sắp xếp theo createdAt
            String order = params.get("order");
            if ("asc".equalsIgnoreCase(order)) {
                query.orderBy(builder.asc(root.get("createdAt")));
            } else {
                query.orderBy(builder.desc(root.get("createdAt"))); // mặc định: mới nhất
            }
        }

        Query q = session.createQuery(query);

        int PAGE_SIZE = Integer.parseInt(env.getProperty("PAGE_SIZE"));

        if (params != null && params.containsKey("page")) {
            int page = Integer.parseInt(params.get("page"));
            int start = (page - 1) * PAGE_SIZE;

            q.setMaxResults(PAGE_SIZE);
            q.setFirstResult(start);
        }

        return q.getResultList();
    }

    @Override
    public User getUserByUsername(String username) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createNamedQuery("User.findByUsername", User.class);
        q.setParameter("username", username);
        try {
            return (User) q.getSingleResult();
        } catch (NoResultException e) {
            return null; // Trả về null nếu không tìm thấy user
        }

    }

    @Override
    public List<User> getUsersByRole(String role) {
        Session s = this.factory.getObject().getCurrentSession();
        Query q = s.createNamedQuery("User.findByRole", User.class);
        q.setParameter("role", role);

        return q.getResultList(); // Trả về danh sách user theo role
    }

    /**
     *
     * @param u
     * @return
     */
    @Override
    public User addUser(User u) {
        String PASSWORD_DEFAULT = String.valueOf(env.getProperty("PASSWORD_DEFAULT"));
        Session s = this.factory.getObject().getCurrentSession();
        u.setIsActive(Boolean.TRUE);
        s.persist(u);
        s.flush();

        if ("ROLE_ADMIN".equals(u.getRole())) {
            Admin admin = new Admin();
            admin.setUserId(u.getId()); // BẮT BUỘC nếu userId là @Id
            s.persist(admin);
        }

        if ("ROLE_LECTURER".equals(u.getRole())) {
            Lecturer lecturer = new Lecturer();
            lecturer.setUserId(u.getId());
            s.persist(lecturer);
            //24h kể từ lúc tại tài khoản
            Timeout timeout = new Timeout(u.getId());
            s.persist(timeout);

            u.setPassword(this.passwordEncoder.encode(PASSWORD_DEFAULT));
            s.merge(u);

            sendMail(
                    "phongkhamsaigoncare@gmail.com", // FROM
                    u.getUsername(), // TO
                    "Thông báo kích hoạt tài khoản", // SUBJECT
                    "Tài khoản của bạn đã được kích hoạt, vui lòng đăng nhập và thay đổi password ,"
                    + "Mật khẩu mặc định: " + String.valueOf(env.getProperty("PASSWORD_DEFAULT"))
                    + "Nếu trong 24 tiếng bạn không đổi mật khẩu, tài khoản sẽ bị khóa"// BODY
            );

        }
        return u;
    }

    @Override
    public User addUser(User u, String studentCode) {
        Session s = this.factory.getObject().getCurrentSession();
        s.persist(u);
        if ("ROLE_ALUMNI".equals(u.getRole())) {
            Alumni alumni = new Alumni();
            alumni.setUserId(u.getId());
            alumni.setStudentCode(studentCode);
            s.persist(alumni);
        }
        u.setIsActive(Boolean.FALSE); //Phải đợi admin duyệt

        s.merge(u);//cập nhập u sau khi inactive tài khoản sinh viên 
          sendMail(
                    "phongkhamsaigoncare@gmail.com", // FROM
                    u.getUsername(), // TO
                    "Tạo tài khoản thành công", // SUBJECT
                    "Tài khoản của bạn đã tạo thành công, vui lòng đợi admin kích hoạt tài khoản của bạn"
            );
        return u;
    }

    @Override
    public User UpdateUser(User u) {
        Session s = this.factory.getObject().getCurrentSession();
        s.merge(u);
        //Thay đổi mật khẩu
        if ("ROLE_LECTURER".equals(u.getRole())) {
            if (u.getPassword() != null && !u.getPassword().isEmpty()) {
                Timeout timeout = s.get(Timeout.class, u.getId());
                if (timeout != null) {
                    System.out.println("Đã tìm thấy Timeout để xóa!");
                    Lecturer lecturer = s.get(Lecturer.class, u.getId());
                    lecturer.setTimeout(null);
                    s.merge(lecturer);

                    s.remove(timeout);
                    System.out.println("Đã thực hiện xóa!");
                    s.flush();
                }
            }
        }
        return u;
    }

    @Override
    public User UpdateUser(User u, String studentCode) {
        Session s = this.factory.getObject().getCurrentSession();
        User existingUser = s.get(User.class, u.getId());
        if (existingUser == null) {
            throw new IllegalArgumentException("User không tồn tại!");
        }

        //Thay đổi role
        if (!existingUser.getRole().equals(u.getRole())) {
            //Xóa bên role cũ
            // Xóa role cũ dựa trên role của existingUser (chứ không phải role mới của u)
            switch (existingUser.getRole()) {
                case "ROLE_ADMIN":
                    Admin admin = s.get(Admin.class, existingUser.getId());
                    if (admin != null) {
                        s.remove(admin);
                    }
                    break;
                case "ROLE_LECTURER":
                    Lecturer lecturer = s.get(Lecturer.class, existingUser.getId());
                    if (lecturer != null) {
                        s.remove(lecturer);
                    }
                    break;
                case "ROLE_ALUMNI":
                    Alumni alumni = s.get(Alumni.class, existingUser.getId());
                    if (alumni != null) {
                        s.remove(alumni);
                    }
                    break;
            }

            //Tạo role mới
            switch (u.getRole()) {
                case "ROLE_ADMIN":
                    Admin admin = new Admin();
                    admin.setUserId(u.getId());
                    s.persist(admin);
                    break;
                case "ROLE_LECTURER":
                    Lecturer lecturer = new Lecturer();
                    lecturer.setUserId(u.getId());
                    s.persist(lecturer);

                    Timeout timeout = new Timeout(u.getId());
                    s.persist(timeout);
                    break;

                case "ROLE_ALUMNI":
                    Alumni alumni = new Alumni();
                    alumni.setUserId(u.getId());
                    alumni.setStudentCode(studentCode);
                    s.persist(alumni);
                    break;
            }
        }

        String PASSWORD_DEFAULT = String.valueOf(env.getProperty("PASSWORD_DEFAULT"));
        //Thay đổi mật khẩu
        if ("ROLE_LECTURER".equals(u.getRole())) {
            if (!u.getPassword().equals(PASSWORD_DEFAULT) && u.getPassword() != null && !u.getPassword().isEmpty()) {
                Timeout timeout = s.get(Timeout.class, existingUser.getId());
                if (timeout != null) {
                    Lecturer lecturer = s.get(Lecturer.class, existingUser.getId());
                    lecturer.setTimeout(null);
                    s.merge(lecturer);

                    s.remove(timeout); // Xóa thời gian cũ
                    s.flush(); // Cập nhật DB ngay                    
                    u.setIsActive(Boolean.TRUE);
                }
            }
        }

        if (u.getPassword() == null || u.getPassword().isEmpty()) {
            u.setPassword(existingUser.getPassword()); // Giữ mật khẩu cũ
        } else {
            u.setPassword(this.passwordEncoder.encode(u.getPassword())); // Mã hóa mật khẩu mới
        }
        s.merge(u);
        return u;
    }

    @Override
    public void deleteUser(int id) {
        Session s = this.factory.getObject().getCurrentSession();
        User u = this.getUserById(id);
        s.remove(u);
    }

    @Override
    public boolean authenticate(String username, String password) {
        User u = this.getUserByUsername(username);

        return this.passwordEncoder.matches(password, u.getPassword());
    }

    @Override
    public User getUserById(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(User.class, id);
    }

    @Override
    public void checkActiveAccount() {
        Session session = factory.getObject().getCurrentSession();

        // Lấy danh sách tất cả các Lecturer có timeout
        List<Timeout> timeouts = session.createQuery("SELECT t FROM Timeout t", Timeout.class).getResultList();

        Date currentTime = new Date();

        for (Timeout timeout : timeouts) {
            if (timeout.getExpiredAt().before(currentTime)) {
                // Nếu timeout đã hết hạn, cập nhật trạng thái User
                Lecturer lecturer = timeout.getLecturer();
                User user = lecturer.getUser();
                user.setIsActive(false);
                session.merge(user);

                lecturer.setTimeout(null);//chỉnh lại null sau khi inactive tài khỏn
                session.merge(lecturer);

                session.remove(timeout);
                session.flush(); // Cập nhật DB ngay
            }
        }
    }

    @Override
    public void activeAccount(int userId) {
        String PASSWORD_DEFAULT = String.valueOf(env.getProperty("PASSWORD_DEFAULT"));

        Session session = factory.getObject().getCurrentSession();
        User u = this.getUserById(userId);
        u.setIsActive(Boolean.TRUE);

        if ("ROLE_LECTURER".equals(u.getRole())) {
            Lecturer lecturer = u.getLecturer();
            //kiểm tra có timeout nào liên kết với lecturer không thì xóa đi để tạo time out mới nếu không có thì tạo mới
            if (lecturer.getTimeout() != null) {
                lecturer.setTimeout(null);
                session.merge(lecturer);
            }
            Timeout timeout = new Timeout(lecturer.getUserId());
            session.persist(timeout);

            u.setPassword(this.passwordEncoder.encode(PASSWORD_DEFAULT));
        }
        session.merge(u);

        //Tiến hành thực hiện gửi mail 
        sendMail(
                "phongkhamsaigoncare@gmail.com", // FROM
                u.getUsername(), // TO
                "Đã kích hoạt tài khoản", // SUBJECT
                "Tài khoản của bạn đã được kích hoạt, vui lòng đăng nhập để thực hiện" // BODY
        );
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
    public Map<String, List<User>> getAllUsersGroupedByRole() {
        Map<String, List<User>> result = new HashMap<>();
        for (Role r : Role.values()) {
            List<User> users = getUsersByRole(r.name()); // Gọi lại hàm bạn đã có
            result.put(r.name(), users);
        }
        return result;
    }
    
       public List<Post> getPosts(int userId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Post> q = b.createQuery(Post.class);
        Root root = q.from(Post.class);
        q.select(root);

        q.where(b.equal(root.get("userId").as(Integer.class), userId));

        Query query = s.createQuery(q);
        return query.getResultList();
    }

    public List<Comment> getComments(int userId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Comment> q = b.createQuery(Comment.class);
        Root root = q.from(Comment.class);
        q.select(root);

        q.where(b.equal(root.get("userId").as(Integer.class), userId));

        Query query = s.createQuery(q);
        return query.getResultList();
    }

    public List<Reaction> getReactions(int userId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Reaction> q = b.createQuery(Reaction.class);
        Root root = q.from(Reaction.class);
        q.select(root);

        q.where(b.equal(root.get("userId").as(Integer.class), userId));

        Query query = s.createQuery(q);
        return query.getResultList();
    }

    public List<Responseoption> getResponseoptions(int userId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Responseoption> q = b.createQuery(Responseoption.class);
        Root root = q.from(Responseoption.class);
        q.select(root);

        q.where(b.equal(root.get("userId").as(Integer.class), userId));

        Query query = s.createQuery(q);
        return query.getResultList();
    }

    public List<Admin> getAdmins(int userId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Admin> q = b.createQuery(Admin.class);
        Root root = q.from(Admin.class);
        q.select(root);

        q.where(b.equal(root.get("userId").as(Integer.class), userId));

        Query query = s.createQuery(q);
        return query.getResultList();
    }

    public List<Lecturer> getLecturers(int userId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Lecturer> q = b.createQuery(Lecturer.class);
        Root root = q.from(Lecturer.class);
        q.select(root);

        q.where(b.equal(root.get("userId").as(Integer.class), userId));

        Query query = s.createQuery(q);
        return query.getResultList();
    }

    public List<Alumni> getAlumnis(int userId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Alumni> q = b.createQuery(Alumni.class);
        Root root = q.from(Alumni.class);
        q.select(root);

        q.where(b.equal(root.get("userId").as(Integer.class), userId));

        Query query = s.createQuery(q);
        return query.getResultList();
    }

    public User getUserByPostId(int postId) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Post> q = b.createQuery(Post.class);
        Root root = q.from(Post.class);

        q.select(root.get("user")); // lấy user từ post
        q.where(b.equal(root.get("id"), postId));

        Query query = session.createQuery(q);
        return (User) query.getSingleResult();
    }
    
      @Override
    public List<User> getUsersByIds(List<Integer> ids) {
        List<User> result = new ArrayList<>();
        Session s = this.factory.getObject().getCurrentSession();
        for (Integer id : ids) {
            User u = s.createNamedQuery("User.findById", User.class)
                    .setParameter("id", id)
                    .getSingleResult();
            result.add(u);
        }
        return result;
    }


}
