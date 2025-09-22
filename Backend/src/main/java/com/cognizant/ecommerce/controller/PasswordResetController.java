package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.passwordreset.PasswordResponseDTO;
import com.cognizant.ecommerce.dto.user.PasswordResetConfirmation;
import com.cognizant.ecommerce.dto.user.PasswordResetRequest;
import com.cognizant.ecommerce.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/password")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @Autowired
    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<PasswordResponseDTO> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        PasswordResponseDTO response = passwordResetService.requestPasswordReset(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<PasswordResponseDTO> confirmPasswordReset(@Valid @RequestBody PasswordResetConfirmation confirmation) {
        PasswordResponseDTO response = passwordResetService.confirmPasswordReset(confirmation);
        return ResponseEntity.ok(response);
    }
}