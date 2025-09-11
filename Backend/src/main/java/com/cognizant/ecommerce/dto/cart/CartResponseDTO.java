package com.cognizant.ecommerce.dto.cart;

import com.cognizant.ecommerce.dto.cartItem.CartItemResponseDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {
    private Long id;
    private Date createdAt;
    private Date updatedAt;
    private BigDecimal totalPrice;
    private List<CartItemResponseDTO> items;
}
