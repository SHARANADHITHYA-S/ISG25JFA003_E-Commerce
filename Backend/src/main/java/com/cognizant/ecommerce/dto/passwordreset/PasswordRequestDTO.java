package com.cognizant.ecommerce.dto.passwordreset;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// This DTO is used to request a password reset
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRequestDTO {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "New password cannot be blank")
    private String newPassword;
}

