package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.service.EmailService;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    @Override
    public void sendPasswordResetEmail(String to, String token) {
        System.out.println("Sending password reset email to: " + to);
        System.out.println("Reset Token: " + token);
        // Here you would use a library like JavaMailSender to send the actual email.
    }
}