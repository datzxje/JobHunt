package com.jobhunt.service;

public interface EmailService {
    void sendVerificationEmail(String toEmail, String verificationCode);
}
