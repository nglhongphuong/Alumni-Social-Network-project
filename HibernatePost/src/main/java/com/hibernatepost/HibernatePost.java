/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.hibernatepost;


import com.post.pojo.Lecturer;
import com.post.pojo.ResponseOption;
import com.post.pojo.SurveyOption;
import com.post.pojo.SurveyPost;
import com.post.pojo.SurveyQuestion;
import com.post.pojo.User;
import com.post.pojo.User.Gender;
import com.post.pojo.User.Role;
import com.post.reponsitories.impl.AdminReponsitory;
import com.post.reponsitories.impl.AlumniReponsitory;
import com.post.reponsitories.impl.CommentReponsitory;
import com.post.reponsitories.impl.InvitationPostReponsitorty;
import com.post.reponsitories.impl.InvitationRecipientReponsitory;
import com.post.reponsitories.impl.LecturerReponsitory;
import com.post.reponsitories.impl.PostsReponsitory;
import com.post.reponsitories.impl.SurveyOptionReponsitory;
import com.post.reponsitories.impl.SurveyPostReponsitory;
import com.post.reponsitories.impl.SurveyQuestionReponsitory;
import com.post.reponsitories.impl.TimeOutReponsitory;
import com.post.reponsitories.impl.UserReponsitory;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.Session;

/**
 *
 * @author ASUS
 */
public class HibernatePost {

    public static void main(String[] args) {
//        System.out.println("Hello World!");
//        UserReponsitory s = new UserReponsitory();
//        s.getUser().forEach(c -> System.out.println(c.getName()));
//
//        AdminReponsitory a = new AdminReponsitory();
//        a.getAdmin().forEach(c -> System.out.println(c.getUserId()));
//
//        AlumniReponsitory al = new AlumniReponsitory();
//        al.getAlumni().forEach(c -> System.out.println(c.getUserId()));
//
//        LecturerReponsitory l = new LecturerReponsitory();
//        l.getLecturer().forEach(c -> System.out.println(c.getUserId()));
//
//        TimeOutReponsitory t = new TimeOutReponsitory();
//        t.getTimeOut().forEach(c -> System.out.println(c.getExpiredAt()));
//
//        PostsReponsitory p = new PostsReponsitory();
//        p.getPosts().forEach(post -> System.out.printf("ID: %d, Content: %s, Created At: %s%n, visibility %s",
//                post.getId(), post.getContent(), post.getCreatedAt(), post.getVisibility()));
//
//        CommentReponsitory c = new CommentReponsitory();
//        c.getComment().forEach(o -> System.out.println(o.getContent()));
//
////Xem thống  kê lượt tương tác trên 1 bài đăng.===========================================================
//        try (Session session = HibernateUtils.getFACTORY().openSession()) {
//            // Tạo đối tượng PostsRepository để gọi phương thức getPostsWithReactionCounts
//            PostsReponsitory postsRepository = new PostsReponsitory();
//
//            // Gọi phương thức và lấy danh sách kết quả
//            List<Object[]> results = postsRepository.getPostsWithReactionCounts();
//
//            // In kết quả
//            for (Object[] result : results) {
//                Integer postId = (Integer) result[0];
//                Long likeCount = (Long) result[1];
//                Long loveCount = (Long) result[2];
//                Long angryCount = (Long) result[3];
//
//                System.out.println("Post ID: " + postId);
//                System.out.println("LIKE Count: " + likeCount);
//                System.out.println("LOVE Count: " + loveCount);
//                System.out.println("ANGRY Count: " + angryCount);
//                System.out.println("---------------------------");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        SurveyPostReponsitory m = new SurveyPostReponsitory();
//        m.getSurveyPost().forEach(x -> System.out.println(x.getAdmin()));
//        SurveyQuestionReponsitory n = new SurveyQuestionReponsitory();
//        n.getSurveyQuestion().forEach(x -> System.out.println(x.toString()));
//        SurveyOptionReponsitory d = new SurveyOptionReponsitory();
//        d.getSurveyOption().forEach(x -> System.out.println(x.toString()));
////====================== Truy vấn survey -- user 
//        SurveyPostReponsitory repo = new SurveyPostReponsitory();
//        List<SurveyPost> posts = repo.getSurveyPostWithResponses();
//
//        for (SurveyPost post : posts) {
//            System.out.println("Survey: " + post.getTitle());
//
//            if (post.getSurveyQuestion() != null) {
//                for (SurveyQuestion q : post.getSurveyQuestion()) {
//                    System.out.println("  Question: " + q.getContent());
//
//                    if (q.getSurveyOption() != null) {
//                        for (SurveyOption option : q.getSurveyOption()) {
//                            System.out.println("    Option: " + option.getContent());
//
//                            if (option.getResponseOption() != null) {
//                                for (ResponseOption ro : option.getResponseOption()) {
//                                    System.out.println("      Answered by User ID: " + ro.getUserId().getId()
//                                            + " at " + ro.getRespondedAt());
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
////================================================================================================
//        InvitationPostReponsitorty c1 = new InvitationPostReponsitorty();
//        c1.getInvitationPost().forEach(o -> System.out.println(o.toString()));
//        InvitationRecipientReponsitory a1 = new InvitationRecipientReponsitory();
//        a1.getInvitationRecipient().forEach(o -> System.out.println(o.toString()));
////==================== Test thêm giảng viên =====================================================
 // Tạo instance của LecturerRepository
        LecturerReponsitory lecturerRepository = new LecturerReponsitory();

        // Tạo đối tượng User giảString username, String name, LocalDate birthday, Gender gender,
//            String password, Role role, String bio, boolean isActive,
//            String avatar, String coverPhoto
             User fakeUser = new User(
            "Lancuoicung@gmail.com",    // username
            "Giao vien vuive",           // name
            LocalDate.of(1995, 5, 20),         // birthday
            Gender.FEMALE,                     // gender
            "password123",                      // password
            Role.LECTURER,                      // role
            "Giảng viên Vui ahhhh",      // bio
            true,                               // isActive
            "avatar.png",                       // avatar
            "coverphoto.jpg"                    // coverPhoto
        );
        // Tạo Lecturer từ User giả
        Lecturer fakeLecturer = new Lecturer(fakeUser);

        // Thêm giảng viên vào cơ sở dữ liệu
        lecturerRepository.addLecturer(fakeLecturer);

        // Lấy danh sách giảng viên từ cơ sở dữ liệu
        List<Lecturer> lecturers = lecturerRepository.getLecturer();

        // In kết quả ra màn hình
        if (lecturers != null && !lecturers.isEmpty()) {
            for (Lecturer lecturer : lecturers) {
                System.out.println("Lecturer ID: " + lecturer.getUserId());
                System.out.println("Lecturer Name: " + lecturer.getUser().getName());
                System.out.println("Lecturer Username: " + lecturer.getUser().getUsername());
                System.out.println("TimeOut ExpiredAt: " + lecturer.getTimeOut().getExpiredAt());
                System.out.println("-----------------------------");
            }
        } else {
            System.out.println("No lecturers found.");
        }

    }
}
