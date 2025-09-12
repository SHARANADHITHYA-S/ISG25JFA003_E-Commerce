package com.cognizant.ecommerce.dto.orderItem;

import lombok.Data;

import java.math.BigDecimal;

@Data

public class OrderItemRequestDTO {
    private Long productId;
    private int quantity;
    private BigDecimal price;
}
