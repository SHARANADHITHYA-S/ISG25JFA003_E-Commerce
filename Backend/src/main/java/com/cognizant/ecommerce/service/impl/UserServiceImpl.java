package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.config.JwtUtil;
import com.cognizant.ecommerce.dao.UserRepository;
import com.cognizant.ecommerce.dto.ForgotPassword.ResetPasswordRequest;
import com.cognizant.ecommerce.dto.user.UserRequestDTO;
import com.cognizant.ecommerce.dto.user.UserResponseDTO;
import com.cognizant.ecommerce.exception.ResourceNotFoundException;
import com.cognizant.ecommerce.exception.TokenMismatchException;
import com.cognizant.ecommerce.model.User;
import com.cognizant.ecommerce.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserResponseDTO registerUser(UserRequestDTO userRequestDTO) {
        if (userRepository.findByEmail(userRequestDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }
        if (userRepository.findByName(userRequestDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("User with this name already exists");
        }
        User user = new User();
        user.setName(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword_hash(passwordEncoder.encode(userRequestDTO.getPassword()));
        user.setRole("USER"); // hardcoded

        User savedUser= userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Override
    public UserResponseDTO updateUserProfile(Long userId, UserRequestDTO userRequestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        user.setName(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    @Override
    public UserResponseDTO findUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        return convertToDto(user);
    }

    @Override
    public List<UserResponseDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private UserResponseDTO convertToDto(User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(user.getId());
        userResponseDTO.setName(user.getName());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setRole(user.getRole());
        userResponseDTO.setCreated_at(user.getCreated_at());
        userResponseDTO.setUpdated_at(user.getUpdated_at());
        return userResponseDTO;
    }

    public String generateResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return jwtUtil.generateResetToken(user.getName());
    }

    public void resetPassword(ResetPasswordRequest request) {
        String usernameFromToken = jwtUtil.extractUsername(request.getToken());

        if (!usernameFromToken.equals(request.getUsername())) {
            throw new TokenMismatchException("Token does not match username");
        }

        User user = userRepository.findByName(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));


        user.setPassword_hash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public void deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        userRepository.delete(user);
        System.out.println("✅ User deleted: ID " + userId);
    }


}