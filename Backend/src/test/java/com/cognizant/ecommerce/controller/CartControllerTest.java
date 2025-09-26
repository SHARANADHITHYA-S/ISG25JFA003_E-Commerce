package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.cart.CartResponseDTO;
import com.cognizant.ecommerce.service.AuthService;
import com.cognizant.ecommerce.service.CartService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private AuthService authService;

    @Test
    @WithMockUser(roles = "USER")
    void getCartByUserId_shouldReturnOk() throws Exception {
        CartResponseDTO response = new CartResponseDTO();

        Mockito.when(authService.isSelfOrAdmin(1L)).thenReturn(true);
        Mockito.when(cartService.getCartByUserId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/carts/user/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteCartByUserId_shouldReturnOk() throws Exception {
        Mockito.when(authService.isSelfOrAdmin(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/carts/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cart deleted"));
    }
}
