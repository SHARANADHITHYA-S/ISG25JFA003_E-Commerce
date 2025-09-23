package com.cognizant.ecommerce.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data; // Consider using Lombok to simplify code

public class PasswordResetConfirmation {

    @NotEmpty(message = "Token is required")
    private String token;

    @NotEmpty(message = "New password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String newPassword;

    @NotEmpty(message = "Confirm password is required")
    private String confirmPassword; // <-- Add this field

    // Getters and Setters for all fields
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    // Add Getter and Setter for the new field
    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}