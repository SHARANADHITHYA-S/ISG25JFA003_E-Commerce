package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.order.OrderRequestDTO;
import com.cognizant.ecommerce.dto.order.OrderResponseDTO;
import com.cognizant.ecommerce.service.AuthService;
import com.cognizant.ecommerce.service.CartService;
import com.cognizant.ecommerce.service.OrderService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private CartService cartService;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "USER")
    void createOrder_shouldReturnCreated() throws Exception {
        OrderRequestDTO request = new OrderRequestDTO();
        request.setUserid(1L);
        request.setAddressId(2L);
        request.setPaymentMethodId(3L);

        OrderResponseDTO response = new OrderResponseDTO();
        response.setId(10L);

        Mockito.when(authService.isSelfOrAdmin(1L)).thenReturn(true);
        Mockito.when(orderService.createOrder(1L, 2L, 3L)).thenReturn(response);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getOrderById_shouldReturnOk() throws Exception {
        OrderResponseDTO response = new OrderResponseDTO();
        response.setId(10L);

        Mockito.when(authService.canAccessOrder(10L)).thenReturn(true);
        Mockito.when(orderService.getOrderById(10L)).thenReturn(response);

        mockMvc.perform(get("/api/orders/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllOrders_shouldReturnOk() throws Exception {
        OrderResponseDTO response = new OrderResponseDTO();
        response.setId(1L);

        Mockito.when(orderService.getAllOrders()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/orders/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getOrdersByUserId_shouldReturnOk() throws Exception {
        OrderResponseDTO response = new OrderResponseDTO();
        response.setId(1L);

        Mockito.when(authService.isSelfOrAdmin(1L)).thenReturn(true);
        Mockito.when(orderService.getOrdersByUserId(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/orders/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateOrderStatus_shouldReturnOk() throws Exception {
        // Prepare mock response
        OrderResponseDTO response = new OrderResponseDTO();
        response.setId(10L);
        response.setStatus("SHIPPED");

        // Mock both service calls
        Mockito.when(orderService.updateOrderStatus(10L, "SHIPPED")).thenReturn(response);
        Mockito.when(orderService.getOrderById(10L)).thenReturn(response);

        // Perform the request and validate response
        mockMvc.perform(put("/api/orders/admin/10/status")
                        .param("status", "SHIPPED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteOrder_shouldReturnOk() throws Exception {
        OrderResponseDTO response = new OrderResponseDTO();
        response.setId(10L);
        response.setStatus("CANCELLED");

        Mockito.when(orderService.deleteOrder(10L)).thenReturn(response);

        mockMvc.perform(put("/api/orders/admin/10"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order Deleted"));
    }
}
