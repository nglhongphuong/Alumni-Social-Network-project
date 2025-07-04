/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.post.repositories;

import com.post.enums.Role;
import com.post.pojo.User;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ASUS
 */
public interface UserRepository {

    List<User> getUsers(Map<String, String> params);

    User getUserByUsername(String username);

    List<User> getUsers();
    
    List<User> getUsersByRole(String role);

    User addUser(User u);

    User addUser(User u, String studentCode);

    void deleteUser(int id);

    boolean authenticate(String username, String password);

    User getUserById(int id);

    User UpdateUser(User u);

    User UpdateUser(User u, String studentCode);

    void checkActiveAccount();

    void activeAccount(int userId);
    
    Map<String, List<User>> getAllUsersGroupedByRole();
    
    List<User> getUsersByIds(List<Integer> ids);
    
    
}
