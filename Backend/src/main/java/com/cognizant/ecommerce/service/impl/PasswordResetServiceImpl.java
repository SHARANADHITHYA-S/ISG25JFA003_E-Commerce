package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.PasswordResetRepository;
import com.cognizant.ecommerce.dao.UserRepository;
import com.cognizant.ecommerce.dto.passwordreset.PasswordResponseDTO;
import com.cognizant.ecommerce.dto.user.PasswordResetConfirmation;
import com.cognizant.ecommerce.dto.user.PasswordResetRequest;
import com.cognizant.ecommerce.model.PasswordResetToken;
import com.cognizant.ecommerce.model.User;
import com.cognizant.ecommerce.service.EmailService;
import com.cognizant.ecommerce.service.PasswordResetService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
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
        Optional<User> userOptional = userRepository.findByEmailIgnoreCase(email);

        System.out.println("Searching for email: " + email);
        System.out.println("User found? " + userOptional.isPresent());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // ✅ Invalidate old tokens
            user.getPasswordResetTokens().forEach(token -> token.setUsed(true));

            // Create new token
            String tokenValue = UUID.randomUUID().toString();
            LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);

            PasswordResetToken token = new PasswordResetToken();
            token.setResetToken(tokenValue);
            token.setUser(user);
            token.setExpiresAt(expiresAt);
            token.setUsed(false);

            passwordResetRepository.save(token);

            System.out.println("Token saved to DB: " + tokenValue);

            // Send email (uncomment when ready)
            // emailService.sendPasswordResetEmail(user.getEmail(), tokenValue);

            return new PasswordResponseDTO(
                    "Password reset link has been sent to your email.",
                    user.getEmail(),
                    tokenValue
            );
        }

        return new PasswordResponseDTO(
                "If an account with that email exists, a password reset link has been sent.",
                email,
                null
        );
    }

    @Override
    public PasswordResponseDTO confirmPasswordReset(PasswordResetConfirmation confirmation) {
        Optional<PasswordResetToken> tokenOptional = passwordResetRepository.findByResetToken(confirmation.getToken());

        if (tokenOptional.isEmpty()) {
            return new PasswordResponseDTO("Invalid or expired token.", null, null);
        }

        PasswordResetToken token = tokenOptional.get();
        if (token.isUsed() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            return new PasswordResponseDTO("Invalid or expired token.", token.getUser().getEmail(), null);
        }

        User user = token.getUser();
        user.setPassword_hash(passwordEncoder.encode(confirmation.getNewPassword()));
        userRepository.save(user);

        token.setUsed(true);
        passwordResetRepository.save(token);

        return new PasswordResponseDTO("Password has been reset successfully.", user.getEmail(), null);
    }
}