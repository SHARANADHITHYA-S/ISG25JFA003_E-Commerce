package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.payment.PaymentRequestDTO;
import com.cognizant.ecommerce.dto.payment.PaymentResponseDTO;
import com.cognizant.ecommerce.service.AuthService;
import com.cognizant.ecommerce.service.PaymentService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "USER")
    void createPayment_shouldReturnOk() throws Exception {
        PaymentRequestDTO request = new PaymentRequestDTO();
        request.setUserId(1L);
        request.setOrderId(2L);
        request.setAmount(BigDecimal.valueOf(999.99));


        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setId(10L);

        Mockito.when(authService.isSelfOrAdmin(1L)).thenReturn(true);
        Mockito.when(paymentService.createPayment(Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPaymentById_shouldReturnOk() throws Exception {
        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setId(10L);

        Mockito.when(authService.canAccessPayment(10L)).thenReturn(true);
        Mockito.when(paymentService.getPaymentById(10L)).thenReturn(response);

        mockMvc.perform(get("/api/payments/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPaymentByOrderId_shouldReturnOk() throws Exception {
        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setId(10L);

        Mockito.when(authService.canAccessOrder(2L)).thenReturn(true);
        Mockito.when(paymentService.getPaymentByOrderId(2L)).thenReturn(response);

        mockMvc.perform(get("/api/payments/order/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePaymentStatus_shouldReturnOk() throws Exception {
        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setId(10L);
        response.setStatus("COMPLETED");

        Mockito.when(paymentService.updatePaymentStatus(10L, "COMPLETED")).thenReturn(response);

        mockMvc.perform(put("/api/payments/admin/10/status")
                        .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllPayments_shouldReturnOk() throws Exception {
        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setId(10L);

        Mockito.when(paymentService.getAllPayments()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/payments/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10));
    }
}
