package com.cognizant.ecommerce;

import com.cognizant.ecommerce.config.JwtUtil;
import com.cognizant.ecommerce.dao.UserRepository;
import com.cognizant.ecommerce.dto.cart.CartResponseDTO;
import com.cognizant.ecommerce.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CartControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setupUsers() {
        userRepository.deleteAll();

        User user1 = new User();
        user1.setName("user1");
        user1.setPassword_hash(passwordEncoder.encode("pass1"));
        user1.setRole("USER");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("user2");
        user2.setPassword_hash(passwordEncoder.encode("pass2"));
        user2.setRole("USER");
        userRepository.save(user2);
    }

    @Test
    void testAccessOwnCart_ShouldSucceed() throws Exception {
        User user = userRepository.findByName("user1").orElseThrow();
        String token = jwtUtil.generateToken(user.getId(), user.getName(), user.getRole());

        mockMvc.perform(get("/api/cart/user/{userId}", user.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testAccessOtherUsersCart_ShouldFail() throws Exception {
        User user2 = userRepository.findByName("user2").orElseThrow();
        User user1 = userRepository.findByName("user1").orElseThrow();
        String token = jwtUtil.generateToken(user2.getId(), user2.getName(), user2.getRole());

        mockMvc.perform(get("/api/cart/user/{userId}", user1.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
