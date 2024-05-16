package com.emailms.emailms.controller;

import com.emailms.emailms.entities.Email;
import com.emailms.emailms.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("send")
    private Boolean send(@RequestBody Email email){
        emailService.sendEmail(email.getToEmail(), email.getSubject(), email.getBody());
        return true;
    }
}
