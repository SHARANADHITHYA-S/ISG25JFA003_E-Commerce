package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.config.JwtUtil;
import com.cognizant.ecommerce.dao.UserRepository;
import com.cognizant.ecommerce.dto.ForgotPassword.ResetPasswordRequest;
import com.cognizant.ecommerce.dto.user.UserRequestDTO;
import com.cognizant.ecommerce.dto.user.UserResponseDTO;
import com.cognizant.ecommerce.exception.ResourceNotFoundException;
import com.cognizant.ecommerce.exception.TokenMismatchException;
import com.cognizant.ecommerce.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRequestDTO userRequestDTO;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        // Manually inject the passwordEncoder mock into the userService instance
        // This is a robust way to handle field injection with Mockito.
        userService.passwordEncoder = passwordEncoder;

        user = User.builder()
                .id(1L)
                .name("testUser")
                .email("test@example.com")
                .password_hash("hashedPassword")
                .role("USER")
                .build();

        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setUsername("testUser");
        userRequestDTO.setEmail("test@example.com");
        userRequestDTO.setPassword("password123");

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
        userResponseDTO.setName("testUser");
        userResponseDTO.setEmail("test@example.com");
        userResponseDTO.setRole("USER");
    }

    @Test
    void registerUser_Success() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponseDTO result = userService.registerUser(userRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(userResponseDTO.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_EmailAlreadyExists_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(userRequestDTO));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserProfile_Success() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        userRequestDTO.setUsername("updatedName");

        // Act
        UserResponseDTO result = userService.updateUserProfile(1L, userRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("updatedName", result.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserProfile_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.updateUserProfile(1L, userRequestDTO));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findUserById_Success() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // Act
        UserResponseDTO result = userService.findUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(userResponseDTO.getEmail(), result.getEmail());
    }

    @Test
    void findUserById_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.findUserById(1L));
    }

    @Test
    void findAllUsers_Success() {
        // Arrange
        List<User> userList = List.of(user, User.builder().id(2L).email("user2@example.com").build());
        when(userRepository.findAll()).thenReturn(userList);

        // Act
        List<UserResponseDTO> result = userService.findAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("test@example.com", result.get(0).getEmail());
    }

    @Test
    void generateResetToken_Success() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtUtil.generateResetToken(anyString())).thenReturn("testToken");

        // Act
        String token = userService.generateResetToken("test@example.com");

        // Assert
        assertNotNull(token);
        assertEquals("testToken", token);
    }

    @Test
    void generateResetToken_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.generateResetToken("nonexistent@example.com"));
    }

    @Test
    void resetPassword_Success() {
        // Arrange
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("validToken");
        request.setUsername("testUser");
        request.setNewPassword("newPassword123");

        when(jwtUtil.extractUsername("validToken")).thenReturn("testUser");
        when(userRepository.findByName("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword123")).thenReturn("newHashedPassword");

        // Act
        userService.resetPassword(request);

        // Assert
        verify(userRepository, times(1)).save(user);
        assertEquals("newHashedPassword", user.getPassword_hash());
    }

    @Test
    void resetPassword_TokenMismatch_ThrowsException() {
        // Arrange
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("mismatchToken");
        request.setUsername("testUser");
        request.setNewPassword("newPassword123");

        when(jwtUtil.extractUsername("mismatchToken")).thenReturn("anotherUser");

        // Act & Assert
        assertThrows(TokenMismatchException.class, () -> userService.resetPassword(request));
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void resetPassword_UserNotFound_ThrowsException() {
        // Arrange
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("validToken");
        request.setUsername("testUser");
        request.setNewPassword("newPassword123");

        when(jwtUtil.extractUsername("validToken")).thenReturn("testUser");
        when(userRepository.findByName("testUser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.resetPassword(request));
        verify(userRepository, never()).save(any(User.class));
    }
}
