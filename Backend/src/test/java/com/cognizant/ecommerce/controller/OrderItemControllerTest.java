package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.orderItem.OrderItemRequestDTO;
import com.cognizant.ecommerce.dto.orderItem.OrderItemResponseDTO;
import com.cognizant.ecommerce.service.AuthService;
import com.cognizant.ecommerce.service.OrderItemService;
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
class OrderItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderItemService orderItemService;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void addOrderItem_shouldReturnOk() throws Exception {
        OrderItemRequestDTO request = new OrderItemRequestDTO();
        request.setOrderId(1L);
        request.setProductId(2L);
        request.setQuantity(3);
        request.setPrice(BigDecimal.valueOf(499.99));

        OrderItemResponseDTO response = new OrderItemResponseDTO();
        response.setId(100L);

        Mockito.when(orderItemService.addOrderItem(Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/api/order-items/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100));
    }


    @Test
    @WithMockUser(roles = "USER")
    void getItemsByOrder_shouldReturnOk() throws Exception {
        Mockito.when(authService.canAccessOrder(1L)).thenReturn(true);
        Mockito.when(orderItemService.getOrderItemsByOrderId(1L)).thenReturn(List.of(new OrderItemResponseDTO()));

        mockMvc.perform(get("/api/order-items/order/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateOrderItem_shouldReturnOk() throws Exception {
        OrderItemRequestDTO request = new OrderItemRequestDTO();
        request.setOrderId(1L);
        request.setProductId(2L);
        request.setQuantity(2);
        request.setPrice(BigDecimal.valueOf(299.99));

        OrderItemResponseDTO response = new OrderItemResponseDTO();
        response.setId(1L);

        Mockito.when(orderItemService.updateOrderItem(Mockito.eq(1L), Mockito.any())).thenReturn(response);

        mockMvc.perform(put("/api/order-items/admin/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteOrderItem_shouldReturnOk() throws Exception {
        Mockito.doNothing().when(orderItemService).deleteOrderItem(1L);

        mockMvc.perform(delete("/api/order-items/admin/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order Deleted"));
    }
}
