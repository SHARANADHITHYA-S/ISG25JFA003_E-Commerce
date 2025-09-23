package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.config.JwtUtil;
import com.cognizant.ecommerce.dao.UserRepository;
import com.cognizant.ecommerce.dto.ForgotPassword.ForgotPasswordRequest;
import com.cognizant.ecommerce.dto.ForgotPassword.ResetPasswordRequest;
import com.cognizant.ecommerce.exception.BadCredentialsException;
import com.cognizant.ecommerce.service.UserService;
import com.cognizant.ecommerce.dto.user.UserRequestDTO;
import com.cognizant.ecommerce.dto.user.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, UserRepository userRepository, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        logger.info("Login attempt for username: {}", request.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String role = userDetails.getAuthorities().iterator().next().getAuthority();
            String token = jwtUtil.generateToken(userDetails.getUsername(), role);

            logger.info("Login successful for username: {}", request.getUsername());
            return ResponseEntity.ok(new JwtResponse(token));

        } catch (BadCredentialsException e) {
            logger.warn("Login failed for username: {}", request.getUsername());
            return ResponseEntity.status(401).body(e);
        }
    }

    @PostMapping("/auth/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        logger.info("Registering user with email: {}", userRequestDTO.getEmail());
        UserResponseDTO registeredUser = userService.registerUser(userRequestDTO);
        logger.info("User registered successfully with ID: {}", registeredUser.getId());
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<UserResponseDTO> updateUserProfile(@PathVariable Long id, @Valid @RequestBody UserRequestDTO userRequestDTO) {
        logger.info("Updating user profile for ID: {}", id);
        UserResponseDTO updatedUser = userService.updateUserProfile(id, userRequestDTO);
        logger.info("User profile updated for ID: {}", id);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        logger.info("Fetching user by ID: {}", id);
        UserResponseDTO user = userService.findUserById(id);
        logger.info("User fetched successfully for ID: {}", id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/admin/user")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        logger.info("Fetching all users");
        List<UserResponseDTO> users = userService.findAllUsers();
        logger.info("Total users fetched: {}", users.size());
        return ResponseEntity.ok(users);
    }

    @PostMapping("/auth/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String token = userService.generateResetToken(request.getEmail());
        return ResponseEntity.ok(Map.of("resetToken", token));
    }

    @PostMapping("/auth/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.ok("Password updated successfully");
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    @AllArgsConstructor
    public static class JwtResponse {
        private String token;
    }
}
