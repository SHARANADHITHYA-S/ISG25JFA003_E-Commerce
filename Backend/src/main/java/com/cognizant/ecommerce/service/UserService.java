package com.cognizant.ecommerce.service;

import com.cognizant.ecommerce.dto.ForgotPassword.ResetPasswordRequest;
import com.cognizant.ecommerce.dto.user.UserRequestDTO;
import com.cognizant.ecommerce.dto.user.UserResponseDTO;
import java.util.List;

public interface UserService {
    UserResponseDTO registerUser(UserRequestDTO userRequestDTO);
    UserResponseDTO updateUserProfile(Long userId, UserRequestDTO userRequestDTO);
    UserResponseDTO findUserById(Long userId);
    List<UserResponseDTO> findAllUsers();
    String generateResetToken(String email);
    void resetPassword(ResetPasswordRequest request);
    void deleteUserById(Long userId);

}