package com.cognizant.ecommerce.service;

import com.cognizant.ecommerce.dto.user.UserRequestDTO;
import com.cognizant.ecommerce.dto.user.UserResponseDTO;
import java.util.List;

public interface UserService {
    UserResponseDTO registerUser(UserRequestDTO userRequestDTO);
    UserResponseDTO updateUserProfile(Long userId, UserRequestDTO userRequestDTO);
    UserResponseDTO findUserById(Long userId);
    List<UserResponseDTO> findAllUsers();
}