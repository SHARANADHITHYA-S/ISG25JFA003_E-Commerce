package com.cognizant.ecommerce.service;

public interface EmailService {
    void sendPasswordResetEmail(String to, String token);
}