package com.pollerianorkys.restaurant.service;


public interface EmailService {
    void sendVerificationEmail(String toEmail, String token);
}
