package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.config.JwtUtil;
import com.cognizant.ecommerce.dao.UserRepository;
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

@RestController
@RequestMapping("/api")
public class UserController {

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
        try {
            // Authenticate against DB
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // Get authenticated user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String role = userDetails.getAuthorities().iterator().next().getAuthority();

            // Generate JWT
            String token = jwtUtil.generateToken(userDetails.getUsername(), role);

            return ResponseEntity.ok(new JwtResponse(token));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(e);
        }
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password; // e.g., "USER" or "ADMIN"
    }

    @Data
    @AllArgsConstructor
    public static class JwtResponse {
        private String token;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO registeredUser = userService.registerUser(userRequestDTO);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }


    @PutMapping("/user/{id}")
    public ResponseEntity<UserResponseDTO> updateUserProfile(@PathVariable Long id, @Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO updatedUser = userService.updateUserProfile(id, userRequestDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/admin/user")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }
}