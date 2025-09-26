package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.payment.PaymentMethodRequestDTO;
import com.cognizant.ecommerce.dto.payment.PaymentMethodResponseDTO;
import com.cognizant.ecommerce.service.AuthService;
import com.cognizant.ecommerce.service.PaymentMethodService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentMethodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentMethodService paymentMethodService;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "USER")
    void getPaymentMethodsByUserId_shouldReturnOk() throws Exception {
        PaymentMethodResponseDTO response = new PaymentMethodResponseDTO();
        response.setPaymentMethodId(1L);

        Mockito.when(authService.isSelfOrAdmin(1L)).thenReturn(true);
        Mockito.when(paymentMethodService.getPaymentMethodsByUserId(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/payment-methods/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].paymentMethodId").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPaymentMethodById_shouldReturnOk() throws Exception {
        PaymentMethodResponseDTO response = new PaymentMethodResponseDTO();
        response.setPaymentMethodId(1L);
        response.setCardType("VISA");

        Mockito.when(authService.canAccessPaymentMethod(1L)).thenReturn(true);
        Mockito.when(paymentMethodService.getPaymentMethodById(1L)).thenReturn(Optional.of(response));

        mockMvc.perform(get("/api/payment-methods/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardType").value("VISA"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void addPaymentMethod_shouldReturnCreated() throws Exception {
        PaymentMethodRequestDTO request = new PaymentMethodRequestDTO();
        request.setCardType("VISA");
        request.setCardNumber("4111111111111111");
        request.setExpirationDate("12/06/2028");
        request.setCardholderName("Test User");
        request.setDefault(false);

        PaymentMethodResponseDTO response = new PaymentMethodResponseDTO();
        response.setPaymentMethodId(1L);

        Mockito.when(authService.isSelfOrAdmin(1L)).thenReturn(true);
        Mockito.when(paymentMethodService.addPaymentMethod(Mockito.eq(1L), Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/api/payment-methods/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentMethodId").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void deletePaymentMethod_shouldReturnNoContent() throws Exception {
        Mockito.when(authService.canAccessPaymentMethod(1L)).thenReturn(true);
        Mockito.doNothing().when(paymentMethodService).deletePaymentMethod(1L);

        mockMvc.perform(delete("/api/payment-methods/1"))
                .andExpect(status().isNoContent());
    }
}
