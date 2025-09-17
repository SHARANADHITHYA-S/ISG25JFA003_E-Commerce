package com.cognizant.ecommerce.mapper;

import com.cognizant.ecommerce.dto.cartItem.CartItemResponseDTO;
import com.cognizant.ecommerce.model.CartItem;
import java.util.List;
import java.util.stream.Collectors;

public class CartItemMapper {
    public static CartItemResponseDTO toDto(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }

        CartItemResponseDTO dto = new CartItemResponseDTO();
        dto.setId(cartItem.getId());
        dto.setQuantity(cartItem.getQuantity());

        if (cartItem.getProduct() != null) {
            dto.setProductId(cartItem.getProduct().getId());
            dto.setProductName(cartItem.getProduct().getName());
            dto.setPrice(cartItem.getProduct().getPrice());
            dto.setTotalPrice(cartItem.getProduct().getPrice().multiply(new java.math.BigDecimal(cartItem.getQuantity())));
        }

        return dto;
    }

    public static List<CartItemResponseDTO> toDtoList(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(CartItemMapper::toDto)
                .collect(Collectors.toList());
    }
}