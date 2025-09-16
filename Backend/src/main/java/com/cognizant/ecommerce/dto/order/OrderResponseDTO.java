package com.cognizant.ecommerce.dto.order;

import com.cognizant.ecommerce.dto.address.AddressResponseDTO;
import com.cognizant.ecommerce.dto.orderItem.OrderItemResponseDTO;
import com.cognizant.ecommerce.dto.payment.PaymentMethodResponseDTO;
import com.cognizant.ecommerce.dto.user.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private Long id;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime placed_at;
    private Long userId;
    private Long addressId;
    private Long paymentMethodId;
    private List<OrderItemResponseDTO> orderItems;
}

