/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.post.controllers;

import com.post.service.LCAppEmailService;
import com.post.service.impl.LCAppEmailServiceImpl;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.mail.javamail.JavaMailSender;

/**
 *
 * @author ASUS
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiEmailController {

    @Autowired
    public JavaMailSender mailSender;

    @GetMapping("/secure/send-mail/") //ADMIN
    @ResponseBody
    @CrossOrigin
    public ResponseEntity<String> getEmail(@RequestParam Map<String, String> params) {

        sendMail("phongkhamsaigoncare@gmail.com", "2251010077phuong@ou.edu.vn", "Hello", "Ban da gui mail thanh cong");
        return new ResponseEntity("Send mail successfully", HttpStatus.OK);
    }

    public void sendMail(String from, String to, String subject, String content) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(content);

        mailSender.send(mailMessage); // Gửi email
    }

}

//    @Autowired
//    LCAppEmailService emailService;
//
//    @GetMapping("/secure/send-mail/") //ADMIN
//    @ResponseBody
//    @CrossOrigin
//    public ResponseEntity<String> getEmail( @RequestParam Map<String, String> params) {
//        
//        emailService.sendEmail("2251010077phuong@ou.edu.vn", "Hello");
//        
//        return new ResponseEntity("Send mail successfully", HttpStatus.OK);
//    }
//}
