package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.PasswordResetRepository;
import com.cognizant.ecommerce.dao.UserRepository;
import com.cognizant.ecommerce.dto.passwordreset.PasswordResponseDTO;
import com.cognizant.ecommerce.dto.user.PasswordResetConfirmation;
import com.cognizant.ecommerce.dto.user.PasswordResetRequest;
import com.cognizant.ecommerce.exception.InvalidPasswordException;
import com.cognizant.ecommerce.exception.InvalidTokenException;
import com.cognizant.ecommerce.exception.UserNotFoundException;
import com.cognizant.ecommerce.model.PasswordResetToken;
import com.cognizant.ecommerce.model.User;
import com.cognizant.ecommerce.service.EmailService;
import com.cognizant.ecommerce.service.PasswordResetService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public PasswordResetServiceImpl(UserRepository userRepository,
                                    PasswordResetRepository passwordResetRepository,
                                    PasswordEncoder passwordEncoder,
                                    EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public PasswordResponseDTO requestPasswordReset(PasswordResetRequest request) {
        String email = request.getEmail().trim();
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException("If an account with that email exists, a password reset link has been sent."));

        // Invalidate old tokens for this user
        user.getPasswordResetTokens().forEach(token -> token.setUsed(true));
        passwordResetRepository.saveAll(user.getPasswordResetTokens());

        String tokenValue = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);

        PasswordResetToken token = new PasswordResetToken();
        token.setResetToken(tokenValue);
        token.setUser(user);
        token.setExpiresAt(expiresAt);
        token.setUsed(false);

        passwordResetRepository.save(token);

        // emailService.sendPasswordResetEmail(user.getEmail(), tokenValue);
        return null;
    }

    @Override
    public PasswordResponseDTO confirmPasswordReset(PasswordResetConfirmation confirmation) {
        PasswordResetToken token = passwordResetRepository.findByResetToken(confirmation.getToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired token."));

        if (token.isUsed() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Invalid or expired token.");
        }

        if (!confirmation.getNewPassword().equals(confirmation.getConfirmPassword())) {
            throw new InvalidPasswordException("The new passwords do not match.");
        }

        User user = token.getUser();
        user.setPassword_hash(passwordEncoder.encode(confirmation.getNewPassword()));
        userRepository.save(user);

        token.setUsed(true);
        passwordResetRepository.save(token);
        return null;
    }
}