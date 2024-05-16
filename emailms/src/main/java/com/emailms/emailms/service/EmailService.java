package com.emailms.emailms.service;

public interface EmailService {
    void sendEmail(String toEmail, String subject, String body);
}
