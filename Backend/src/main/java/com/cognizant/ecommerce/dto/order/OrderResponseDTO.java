package com.cognizant.ecommerce.dto.order;

import com.cognizant.ecommerce.dto.orderItem.OrderItemResponseDTO;
import com.cognizant.ecommerce.dto.payment.PaymentMethodResponseDTO;
import com.cognizant.ecommerce.dto.user.UserResponseDTO;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class OrderResponseDTO {
    private Long id;
    private String status;
    private BigDecimal totalAmount;
    private Date placedAt;
    private Date updatedAt;
    private UserResponseDTO user;
    private PaymentMethodResponseDTO DTO;
    private List<OrderItemResponseDTO> orderItems;
}
