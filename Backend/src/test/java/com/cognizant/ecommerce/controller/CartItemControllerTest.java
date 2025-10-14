package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.cartItem.CartItemRequestDTO;
import com.cognizant.ecommerce.dto.cartItem.CartItemResponseDTO;
import com.cognizant.ecommerce.service.CartItemService;
import com.cognizant.ecommerce.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CartItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartItemService cartItemService;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private CartItemRequestDTO sampleRequest;
    private CartItemResponseDTO sampleResponse;

    @BeforeEach
    void setup() {
        sampleRequest = new CartItemRequestDTO();
        sampleRequest.setProductId(1L);
        sampleRequest.setQuantity(2);

        sampleResponse = new CartItemResponseDTO();
        sampleResponse.setId(5L);
        sampleResponse.setProductId(1L);
        sampleResponse.setQuantity(2);
        sampleResponse.setPrice(BigDecimal.valueOf(5000));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createCartItem_shouldReturnCreated() throws Exception {
        Mockito.when(authService.isSelfOrAdmin(1L)).thenReturn(true);
        Mockito.when(cartItemService.createCartItem(Mockito.eq(1L), Mockito.any()))
                .thenReturn(sampleResponse);

        mockMvc.perform(post("/api/cart-items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCartItemById_shouldReturnOk() throws Exception {
        Mockito.when(authService.canAccessCartItem(5L)).thenReturn(true);
        Mockito.when(cartItemService.getCartItemById(5L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/cart-items/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.productId").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllCartItems_shouldReturnList() throws Exception {
        Mockito.when(cartItemService.getAllCartItems()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/cart-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateCartItem_shouldReturnUpdated() throws Exception {
        Mockito.when(authService.canAccessCartItem(5L)).thenReturn(true);
        Mockito.when(cartItemService.updateCartItem(Mockito.eq(5L), Mockito.any()))
                .thenReturn(sampleResponse);

        mockMvc.perform(put("/api/cart-items/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteCartItem_shouldReturnConfirmation() throws Exception {
        Mockito.when(authService.canAccessCartItem(5L)).thenReturn(true);

        mockMvc.perform(delete("/api/cart-items/5"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cart item deleted"));
    }
}
