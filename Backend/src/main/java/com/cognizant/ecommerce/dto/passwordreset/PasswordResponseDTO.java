package com.cognizant.ecommerce.dto.passwordreset;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// This DTO is used for returning a confirmation after a password reset
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResponseDTO {

    private String message;
    private String email;
}