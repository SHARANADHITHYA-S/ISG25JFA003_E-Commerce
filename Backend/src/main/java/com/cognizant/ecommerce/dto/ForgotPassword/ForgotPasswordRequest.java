package com.cognizant.ecommerce.dto.ForgotPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {

    @Email(message = "enter valid email")
    @NotBlank
    private String email;
}
