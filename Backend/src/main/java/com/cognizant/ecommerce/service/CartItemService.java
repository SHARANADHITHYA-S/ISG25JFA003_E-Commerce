package com.cognizant.ecommerce.service;

import com.cognizant.ecommerce.dto.cartItem.CartItemRequestDTO;
import com.cognizant.ecommerce.dto.cartItem.CartItemResponseDTO;
import java.util.List;

public interface CartItemService {
    CartItemResponseDTO createCartItem(Long userId, CartItemRequestDTO cartItemRequestDTO);
    CartItemResponseDTO getCartItemById(Long id);
    List<CartItemResponseDTO> getAllCartItems();
    CartItemResponseDTO updateCartItem(Long id, CartItemRequestDTO cartItemRequestDTO);
    void deleteCartItem(Long id);
}