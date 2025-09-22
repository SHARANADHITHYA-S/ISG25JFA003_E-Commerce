package com.cognizant.ecommerce.service;

import com.cognizant.ecommerce.dto.passwordreset.PasswordResponseDTO;
import com.cognizant.ecommerce.dto.user.PasswordResetConfirmation;
import com.cognizant.ecommerce.dto.user.PasswordResetRequest;

public interface PasswordResetService {
    PasswordResponseDTO requestPasswordReset(PasswordResetRequest request);
    PasswordResponseDTO confirmPasswordReset(PasswordResetConfirmation confirmation);
}