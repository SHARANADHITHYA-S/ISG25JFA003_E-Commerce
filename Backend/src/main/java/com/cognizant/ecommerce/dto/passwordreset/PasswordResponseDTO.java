package com.cognizant.ecommerce.dto.passwordreset;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResponseDTO {
    private String message;
    private String email;
    private String token;

    // Convenience constructor for responses without token
    public PasswordResponseDTO(String message, String email) {
        this.message = message;
        this.email = email;
    }
}
