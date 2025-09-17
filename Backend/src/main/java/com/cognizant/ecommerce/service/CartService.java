package com.cognizant.ecommerce.service;

import com.cognizant.ecommerce.dto.cart.CartResponseDTO;

public interface CartService {
    CartResponseDTO getCartByUserId(Long userId);
    void deleteCartByUserId(Long userId);
}