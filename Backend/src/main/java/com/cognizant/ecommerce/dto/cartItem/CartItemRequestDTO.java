package com.cognizant.ecommerce.dto.cartItem;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequestDTO {
    private Long productId;
    private Integer quantity;
}