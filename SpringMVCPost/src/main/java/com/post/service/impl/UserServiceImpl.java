/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.post.enums.Role;
import com.post.pojo.User;
import com.post.repositories.UserRepository;
import com.post.service.UserService;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ASUS
 */
@Service("userDetailsService")
@PropertySource("classpath:database.properties")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private Environment env;

    @Override
    public List<User> getUsers(Map<String, String> params) {
        return this.userRepo.getUsers(params);
    }

    @Override
    public User getUserByUsername(String username) {
        return this.userRepo.getUserByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = this.getUserByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException("Invalid username!");
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(u.getRole()));

        return new org.springframework.security.core.userdetails.User(
                u.getUsername(), u.getPassword(), authorities);
    }

    @Override
    public User addUser(Map<String, String> params, MultipartFile avatar, MultipartFile coverPhoto) {
        User u = new User();
        u.setName(params.get("name"));
        u.setUsername(params.get("username"));
        u.setRole(params.get("role"));
        u.setBio(params.get("bio"));
        u.setGender(params.get("gender"));
        u.setIsActive(Boolean.valueOf(params.get("isActive")));

        String PASSWORD_DEFAULT = String.valueOf(env.getProperty("PASSWORD_DEFAULT"));
        if (!params.get("password").isEmpty()) {//khác rỗng
            u.setPassword(this.passwordEncoder.encode(params.get("password")));
        } else {
            u.setPassword(this.passwordEncoder.encode(PASSWORD_DEFAULT));//default
        }

        try {
            // Chuyển chuỗi thành java.sql.Date (chỉ lưu phần ngày)
            java.sql.Date sqlDate = java.sql.Date.valueOf(params.get("birthday"));
            u.setBirthday(sqlDate);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (!avatar.isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(avatar.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                u.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (!coverPhoto.isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(avatar.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                u.setCoverPhoto(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (u.getRole().equals("ROLE_ALUMNI")) {
            String studentCode = params.get("studentCode");
            if (studentCode == null || studentCode.isEmpty()) {
                throw new IllegalArgumentException("Alumni cần có mã sinh viên (studentCode)");
            }
            return this.userRepo.addUser(u, studentCode);
        }
        return this.userRepo.addUser(u);
    }

    @Override
    public boolean authenticate(String username, String password) {
        return this.userRepo.authenticate(username, password);
    }

    @Override
    public User getUserById(int id) {
        return this.userRepo.getUserById(id);
    }

    @Override
    public void deleteUser(int id) {
        this.userRepo.deleteUser(id);
    }

    @Override
    public User updateUser(Map<String, String> params, MultipartFile avatar, MultipartFile coverPhoto) {

        User u = new User();
        u.setName(params.get("name"));
        u.setUsername(params.get("username"));
        u.setRole(params.get("role"));
        u.setBio(params.get("bio"));
        u.setGender(params.get("gender"));
        u.setIsActive(Boolean.valueOf(params.get("isActive")));
        u.setId(Integer.valueOf(params.get("id")));
        u.setPassword(params.get("password"));
//        u.setPassword(this.passwordEncoder.encode(params.get("password")));

        try {
            // Chuyển chuỗi thành java.sql.Date (chỉ lưu phần ngày)
            java.sql.Date sqlDate = java.sql.Date.valueOf(params.get("birthday"));
            u.setBirthday(sqlDate);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        // check có file ảnh mới, ko thì giữ ảnh cũ
        if (avatar != null && !avatar.isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(avatar.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                u.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            u.setAvatar(params.get("avatar")); // Giữ ảnh cũ
        }

        if (coverPhoto != null && !coverPhoto.isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(coverPhoto.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                u.setCoverPhoto(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            u.setCoverPhoto(params.get("coverPhoto")); // Giữ ảnh cũ
        }

        if (u.getRole().equals("ROLE_ALUMNI")) {
            String studentCode = params.get("studentCode");
            if (studentCode == null || studentCode.isEmpty()) {
                throw new IllegalArgumentException("Alumni cần có mã sinh viên (studentCode)");
            }
            return this.userRepo.UpdateUser(u, studentCode);
        }
        return this.userRepo.UpdateUser(u, "");
    }

    @Override
    public User UpdateUser(User u) {
        return this.userRepo.UpdateUser(u);
    }

    @Override
    public void checkActiveAccount() {
        this.userRepo.checkActiveAccount();
    }

    @Override
    public void activeAccount(int userId) {
        this.userRepo.activeAccount(userId);
    }

    @Override
    public List<User> getUsers() {
        return this.userRepo.getUsers();
    }

    @Override
    public List<User> getUsersByRole(String role) {
        return this.userRepo.getUsersByRole(role);
    }

    @Override
    public Map<String, List<User>> getAllUsersGroupedByRole() {
        return this.userRepo.getAllUsersGroupedByRole();
    }

    @Override
    public List<User> getUsersByIds(List<Integer> ids) {
       return this.userRepo.getUsersByIds(ids);
    }
}
