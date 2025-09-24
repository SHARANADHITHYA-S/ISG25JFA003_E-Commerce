package com.cognizant.ecommerce.dto.ForgotPassword;


import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String token;
    private String username;
    private String newPassword;
}
