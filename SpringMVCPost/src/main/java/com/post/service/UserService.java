/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.post.service;

import com.post.enums.Role;
import com.post.pojo.User;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ASUS
 */
public interface UserService extends UserDetailsService {
    List<User> getUsers(Map<String, String> params);
    List<User> getUsers() ;
    List<User> getUsersByRole(String role);
    User getUserByUsername(String username);
    User addUser(Map<String, String> params, MultipartFile avatar,MultipartFile coverPhoto);
    User updateUser(Map<String, String> params, MultipartFile avatar,MultipartFile coverPhoto);
    User UpdateUser(User u);
    boolean authenticate(String username, String password);
    User getUserById(int id);
    void deleteUser(int id);
    void checkActiveAccount();
    void activeAccount(int userId);
    Map<String, List<User>> getAllUsersGroupedByRole();
    List<User> getUsersByIds(List<Integer> ids);
}
