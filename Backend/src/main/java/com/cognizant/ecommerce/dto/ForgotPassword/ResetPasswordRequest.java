package com.cognizant.ecommerce.dto.ForgotPassword;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "Enter valid token")
    private String token;
    @NotBlank(message = "Enter valid username")
    private String username;
    @NotBlank(message = "Enter valid password")
    private String newPassword;
}
