package com.cognizant.ecommerce.dao;

import com.cognizant.ecommerce.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordResetToken, Long> {
    // Add this method to allow finding a token by its value
    Optional<PasswordResetToken> findByResetToken(String resetToken);
}