package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dto.cart.CartResponseDTO;
import com.cognizant.ecommerce.dto.cartItem.CartItemResponseDTO;
import com.cognizant.ecommerce.exception.ResourceNotFoundException;
import com.cognizant.ecommerce.model.Cart;
import com.cognizant.ecommerce.model.CartItem;
import com.cognizant.ecommerce.model.Product;
import com.cognizant.ecommerce.dao.CartRepository;
import com.cognizant.ecommerce.dao.CartItemRepository;
import com.cognizant.ecommerce.dao.UserRepository;
import com.cognizant.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public CartResponseDTO getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user with ID: " + userId));
        return convertToCartResponseDTO(cart);
    }

    @Override
    @Transactional
    public void deleteCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user with ID: " + userId));
        cartRepository.delete(cart);
    }

    private CartResponseDTO convertToCartResponseDTO(Cart cart) {
        CartResponseDTO cartResponseDTO = new CartResponseDTO();
        cartResponseDTO.setId(cart.getId());

        List<CartItemResponseDTO> cartItemDTOs = cart.getCartItems().stream()
                .map(this::convertToCartItemResponseDTO)
                .collect(Collectors.toList());

        cartResponseDTO.setItems(cartItemDTOs);

        BigDecimal total = cartItemDTOs.stream()
                .map(CartItemResponseDTO::getTotalPrice)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cartResponseDTO.setTotalPrice(total);
        return cartResponseDTO;
    }

    private CartItemResponseDTO convertToCartItemResponseDTO(CartItem cartItem) {
        Product product = cartItem.getProduct();
        CartItemResponseDTO dto = new CartItemResponseDTO();
        dto.setId(cartItem.getId());
        dto.setProductId(product.getId());
        dto.setProductName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setQuantity(cartItem.getQuantity());
        dto.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        return dto;
    }
}
