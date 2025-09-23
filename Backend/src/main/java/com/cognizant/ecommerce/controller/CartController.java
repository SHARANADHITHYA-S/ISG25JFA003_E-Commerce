package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.cart.CartResponseDTO;
import com.cognizant.ecommerce.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;


    @GetMapping("/user/{userId}")
    public ResponseEntity<CartResponseDTO> getCartByUserId(@PathVariable Long userId) {
        logger.info("Fetching cart for user ID: {}", userId);
        CartResponseDTO cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> deleteCartByUserId(@PathVariable Long userId) {
        logger.info("Deleting cart for user ID: {}", userId);
        cartService.deleteCartByUserId(userId);
        return ResponseEntity.ok("Cart deleted");
    }
}
