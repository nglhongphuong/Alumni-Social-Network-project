/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.post.pojo.User;
import com.post.service.UserService;
import com.post.service.impl.UserServiceImpl;
import com.post.utils.JwtUtils;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ASUS
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiUserController {

    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private Cloudinary cloudinary;

    @PostMapping(path = "/user",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> create(
            @RequestParam Map<String, String> params,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar,
            @RequestParam(value = "coverPhoto", required = false) MultipartFile coverPhoto) {

        String username = params.get("username");
        if (this.userService.getUserByUsername(username) != null) {
            return new ResponseEntity("Username đã tồn tại, vui lòng chọn username khác!", HttpStatus.CONFLICT);
        }

        User u = this.userService.addUser(params, avatar, coverPhoto);
        return new ResponseEntity<>(u, HttpStatus.CREATED);
    }

    @DeleteMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable(value = "userId") int id) //- ROLE_ADMIN
    {
        this.userService.deleteUser(id);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User u) {
        this.userService.checkActiveAccount();
        User currentUser = this.userService.getUserByUsername(u.getUsername());
        if (!currentUser.getIsActive()) {
            return new ResponseEntity("Tài khoản của bạn đã bị vô hiệu hóa. Vui lòng liên hệ quản trị viên để kích hoạt lại.", HttpStatus.FORBIDDEN);
        }

        if (this.userService.authenticate(u.getUsername(), u.getPassword())) {
            try {
                String token = JwtUtils.generateToken(u.getUsername());
                return ResponseEntity.ok().body(Collections.singletonMap("token", token));
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Lỗi khi tạo JWT");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai thông tin đăng nhập");
    }

    @RequestMapping("/secure/profile")
    @ResponseBody
    @CrossOrigin
    public ResponseEntity<User> getProfile(Principal principal) {
        return new ResponseEntity<>(this.userService.getUserByUsername(principal.getName()), HttpStatus.OK);
    }

    @PutMapping(path = "/secure/profile",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateProfile(
            @RequestParam Map<String, String> params,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar,
            @RequestParam(value = "coverPhoto", required = false) MultipartFile coverPhoto,
            Principal principal) {
        User currentUser = this.userService.getUserByUsername(principal.getName());

        if (currentUser == null) {
            return new ResponseEntity("Không tìm thấy người dùng!", HttpStatus.NOT_FOUND);
        }
        if (params.containsKey("name") && !params.get("name").trim().isEmpty()) {
            currentUser.setName(params.get("name"));
        }
        if (params.containsKey("bio") && !params.get("bio").trim().isEmpty()) {
            currentUser.setBio(params.get("bio"));
        }
        if (params.containsKey("gender") && !params.get("gender").trim().isEmpty()) {
            currentUser.setGender(params.get("gender"));
        }
        if (params.containsKey("birthday") && !params.get("birthday").trim().isEmpty()) {
            try {
                LocalDate localDate = LocalDate.parse(params.get("birthday"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                currentUser.setBirthday(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            } catch (DateTimeParseException e) {
                return new ResponseEntity<>("Định dạng ngày không hợp lệ! Vui lòng dùng yyyy-MM-dd", HttpStatus.BAD_REQUEST);
            }

        }

        if (avatar != null && !avatar.isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(avatar.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                currentUser.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) {
                return new ResponseEntity<>("Lỗi khi tải ảnh", HttpStatus.INTERNAL_SERVER_ERROR); // Thêm trạng thái lỗi rõ ràng
            }
        }
        if (coverPhoto != null && !coverPhoto.isEmpty()) {
            try {
                Map res = cloudinary.uploader().upload(coverPhoto.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                currentUser.setCoverPhoto(res.get("secure_url").toString());
            } catch (IOException ex) {
                return new ResponseEntity<>("Lỗi khi tải ảnh", HttpStatus.INTERNAL_SERVER_ERROR); // Thêm trạng thái lỗi rõ ràng
            }
        }

        this.userService.UpdateUser(currentUser);
        return new ResponseEntity("Hồ sơ đã được cập nhật!", HttpStatus.OK);
    }

    @PutMapping("/secure/change-password")
    public ResponseEntity<String> changePassword(@RequestBody Map<String, String> request, Principal principal) {
        User currentUser = this.userService.getUserByUsername(principal.getName());

        if (currentUser == null) {
            return new ResponseEntity("Không tìm thấy người dùng!", HttpStatus.NOT_FOUND);
        }

        String newPassword = request.get("password"); // Lấy dữ liệu từ JSON

        if (newPassword == null || newPassword.trim().isEmpty()) {
            return new ResponseEntity("Mật khẩu không hợp lệ!", HttpStatus.BAD_REQUEST);
        }
        currentUser.setPassword(passwordEncoder.encode(newPassword));
        this.userService.UpdateUser(currentUser);
        return new ResponseEntity("Mật khẩu đã được cập nhật!", HttpStatus.OK);
    }

    @PutMapping("/secure/active-account/{userId}") //Role_ADMIN
    public ResponseEntity<String> activeAccount(Principal principal, @PathVariable("userId") int userId) {

        try {
            this.userService.activeAccount(userId);
            return new ResponseEntity("Tài khoản đã được kích hoạt!", HttpStatus.OK);
        } catch (NullPointerException e) {
            return new ResponseEntity("Lỗi: Không tìm thấy dữ liệu!", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity("Lỗi trong quá trình kích hoạt tài khoản: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/secure/user") //-ROLE_ADMIN
    public ResponseEntity<List<User>> getUser(Principal principal, @RequestParam Map<String, String> params) {
        List<User> users = this.userService.getUsers(params);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/secure/user/{userId}") //ALL
    @ResponseBody
    @CrossOrigin
    public ResponseEntity<User> getUserInfo(Principal principal, @PathVariable(value = "userId") int id) {
        User user = this.userService.getUserById(id);
        if (user == null) {
            return new ResponseEntity("User with ID " + id + " not found", HttpStatus.NOT_FOUND);
        }
        user.setPassword(null);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
