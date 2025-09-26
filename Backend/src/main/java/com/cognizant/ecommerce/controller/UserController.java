package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.config.JwtUtil;
import com.cognizant.ecommerce.dao.UserRepository;
import com.cognizant.ecommerce.dto.ForgotPassword.ForgotPasswordRequest;
import com.cognizant.ecommerce.dto.ForgotPassword.ResetPasswordRequest;
import com.cognizant.ecommerce.exception.BadCredentialsException;
import com.cognizant.ecommerce.exception.ErrorResponse;
import com.cognizant.ecommerce.exception.InvalidCredentialsException;
import com.cognizant.ecommerce.exception.ResourceNotFoundException;
import com.cognizant.ecommerce.model.User;
import com.cognizant.ecommerce.service.UserService;
import com.cognizant.ecommerce.dto.user.UserRequestDTO;
import com.cognizant.ecommerce.dto.user.UserResponseDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserService userService, UserRepository userRepository, AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository1) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository1;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Login attempt for username: {}", request.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String role = userDetails.getAuthorities().iterator().next().getAuthority();

            User user = userRepository.findByName(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String token = jwtUtil.generateToken(user.getId(), userDetails.getUsername(), role);

            logger.info("Login successful for username: {}", request.getUsername());
            return ResponseEntity.ok(new JwtResponse(token));

        } catch (AuthenticationException e) {
            logger.warn("Login failed for username: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        }
    }



    @PostMapping("/auth/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        logger.info("Registering user with email: {}", userRequestDTO.getEmail());
        UserResponseDTO registeredUser = userService.registerUser(userRequestDTO);
        logger.info("User registered successfully with ID: {}", registeredUser.getId());
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PreAuthorize("@authService.isSelfOrAdmin(#userId)")
    @PutMapping("/user/{userId}")
    public ResponseEntity<UserResponseDTO> updateUserProfile(@Valid @PathVariable Long userId, @Valid @RequestBody UserRequestDTO userRequestDTO) {
        logger.info("Updating user profile for ID: {}", userId);
        UserResponseDTO updatedUser = userService.updateUserProfile(userId, userRequestDTO);
        logger.info("User profile updated for ID: {}", userId);
        return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("@authService.isSelfOrAdmin(#userId)")
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long userId) {
        logger.info("Fetching user by ID: {}", userId);
        UserResponseDTO user = userService.findUserById(userId);
        logger.info("User fetched successfully for ID: {}", userId);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/user")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        logger.info("Fetching all users");
        List<UserResponseDTO> users = userService.findAllUsers();
        logger.info("Total users fetched: {}", users.size());
        return ResponseEntity.ok(users);
    }

    @PostMapping("/auth/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        String token = userService.generateResetToken(request.getEmail());
        logger.info("Reset token generated");
        return ResponseEntity.ok(Map.of("resetToken", token));
    }

    @PostMapping("/auth/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request);
        logger.info("Password updated successfully");
        return ResponseEntity.ok("Password updated successfully");
    }


    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteOwnAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object details = auth.getDetails();

        if (!(details instanceof Long userId)) {
            throw new BadCredentialsException("Invalid authentication details");
        }

        userService.deleteUserById(userId);
        return ResponseEntity.ok("Your account has been deleted");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{userId}")
    public ResponseEntity<String> deleteUserByAdmin(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.ok("User deleted by admin");
    }





    @Data
    public static class LoginRequest {

        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Password is required")
        @Pattern(regexp = "^\\S+$", message = "Password must not contain spaces")
        private String password;
    }

    @Data
    @AllArgsConstructor
    public static class JwtResponse {
        private String token;
    }
}
