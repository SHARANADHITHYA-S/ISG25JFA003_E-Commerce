package com.cognizant.ecommerce.dto.order;

import com.cognizant.ecommerce.dto.orderItem.OrderItemRequestDTO;
import lombok.Builder;
import lombok.Data;
import java.util.List;

import java.math.BigDecimal;

@Data
@Builder
public class OrderRequestDTO {
    private String status;
    private BigDecimal totalAmount;
    private Long userid;
    private Long addressId;
    private Long paymentMethodId;
    private List<OrderItemRequestDTO> items;
}
