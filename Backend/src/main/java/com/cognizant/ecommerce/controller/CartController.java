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

    @GetMapping("/user/{userId}")
    public ResponseEntity<CartResponseDTO> getCartByUserId(@PathVariable Long userId) {
        // Security check: ensure the user ID matches the authenticated user.
        // For this example, we assume it's valid.
        CartResponseDTO cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> deleteCartByUserId(@PathVariable Long userId) {
        // Security check: ensure the user ID matches the authenticated user.
        cartService.deleteCartByUserId(userId);
        return ResponseEntity.ok("Cart deleted");
    }

}