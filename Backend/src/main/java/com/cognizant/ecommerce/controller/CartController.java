package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.cart.CartResponseDTO;
import com.cognizant.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    // A placeholder for a method that retrieves the authenticated user's ID.
    // In a real application, this would come from a security context (e.g., Spring Security).
    private Long getCurrentUserId() {
        return 1L; // Hardcoded for this example
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponseDTO> getCartByUserId(@PathVariable Long userId) {
        // Security check: ensure the user ID matches the authenticated user.
        // For this example, we assume it's valid.
        CartResponseDTO cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteCartByUserId(@PathVariable Long userId) {
        // Security check: ensure the user ID matches the authenticated user.
        cartService.deleteCartByUserId(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        // Security check: ensure the user ID matches the authenticated user.
        cartService.deleteCartByUserId(userId);
        return ResponseEntity.noContent().build();
    }
}