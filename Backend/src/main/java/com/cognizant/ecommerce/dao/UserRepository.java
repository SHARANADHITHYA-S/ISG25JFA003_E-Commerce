package com.cognizant.ecommerce.dao;

import com.cognizant.ecommerce.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<Object> findByEmail(@NotBlank(message = "Email cannot be blank") @Email(message = "Email should be valid") String email);
}