package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.config.JwtUtil;
import com.cognizant.ecommerce.dao.UserRepository;
import com.cognizant.ecommerce.dto.ForgotPassword.ForgotPasswordRequest;
import com.cognizant.ecommerce.dto.ForgotPassword.ResetPasswordRequest;
import com.cognizant.ecommerce.dto.user.UserRequestDTO;
import com.cognizant.ecommerce.dto.user.UserResponseDTO;
import com.cognizant.ecommerce.model.User;
import com.cognizant.ecommerce.service.AuthService;
import com.cognizant.ecommerce.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_shouldReturnToken() throws Exception {
        UserController.LoginRequest request = new UserController.LoginRequest();
        request.setUsername("testuser");
        request.setPassword("securePass123");

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("securePass123")
                .roles("USER")
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        User user = new User();
        user.setId(1L);
        user.setName("testuser");

        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(auth);
        Mockito.when(userRepository.findByName("testuser")).thenReturn(Optional.of(user));
        Mockito.when(jwtUtil.generateToken(1L, "testuser", "ROLE_USER")).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
    }

    @Test
    void registerUser_shouldReturnCreated() throws Exception {
        UserRequestDTO request = new UserRequestDTO();
        request.setUsername("John Doe");
        request.setEmail("john@example.com");
        request.setPassword("securePass123");

        UserResponseDTO response = new UserResponseDTO();
        response.setId(1L);
        response.setName("John Doe");

        Mockito.when(userService.registerUser(Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateUserProfile_shouldReturnOk() throws Exception {
        UserRequestDTO request = new UserRequestDTO();
        request.setUsername("Updated Name");
        request.setEmail("updated@example.com");
        request.setPassword("newPass123");

        UserResponseDTO response = new UserResponseDTO();
        response.setId(1L);
        response.setName("Updated Name");

        Mockito.when(authService.isSelfOrAdmin(1L)).thenReturn(true);
        Mockito.when(userService.updateUserProfile(Mockito.eq(1L), Mockito.any())).thenReturn(response);

        mockMvc.perform(put("/api/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name")); // ✅ corrected path
    }


    @Test
    @WithMockUser(roles = "USER")
    void getUserById_shouldReturnOk() throws Exception {
        UserResponseDTO response = new UserResponseDTO();
        response.setId(1L);
        response.setName("John Doe");

        Mockito.when(authService.isSelfOrAdmin(1L)).thenReturn(true); // ✅ mock access check
        Mockito.when(userService.findUserById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_shouldReturnOk() throws Exception {
        UserResponseDTO response = new UserResponseDTO();
        response.setId(1L);
        response.setName("Admin User");

        Mockito.when(userService.findAllUsers()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/admin/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Admin User"));
    }

    @Test
    void forgotPassword_shouldReturnToken() throws Exception {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("john@example.com");

        Mockito.when(userService.generateResetToken("john@example.com")).thenReturn("reset-token-123");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resetToken").value("reset-token-123"));
    }

    @Test
    void resetPassword_shouldReturnOk() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("reset-token-123");
        request.setUsername("john@example.com"); // ✅ required field
        request.setNewPassword("newSecurePass");

        Mockito.doNothing().when(userService).resetPassword(Mockito.any());

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password updated successfully"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUserByAdmin_shouldReturnOk() throws Exception {
        Mockito.doNothing().when(userService).deleteUserById(1L);

        mockMvc.perform(delete("/api/admin/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted by admin"));
    }
}
