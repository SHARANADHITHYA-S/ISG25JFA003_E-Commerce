package com.cognizant.ecommerce.dto.cart;

import com.cognizant.ecommerce.dto.cartItem.CartItemRequestDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartRequestDTO {
    private List<CartItemRequestDTO> items;
}
