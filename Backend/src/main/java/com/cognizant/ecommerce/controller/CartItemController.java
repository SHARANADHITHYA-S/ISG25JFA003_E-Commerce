package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.cartItem.CartItemRequestDTO;
import com.cognizant.ecommerce.dto.cartItem.CartItemResponseDTO;
import com.cognizant.ecommerce.service.CartItemService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart-items")
public class CartItemController {

    private static final Logger logger = LoggerFactory.getLogger(CartItemController.class);

    @Autowired
    private CartItemService cartItemService;

    // Only the owner or admin can create a cart item
    @PreAuthorize("@authService.isSelfOrAdmin(#userId)")
    @PostMapping("/{userId}")
    public ResponseEntity<CartItemResponseDTO> createCartItem(@Valid @PathVariable Long userId, @Valid @RequestBody CartItemRequestDTO cartItemRequestDTO) {
        logger.info("Creating cart item for user ID: {}", userId);
        CartItemResponseDTO newCartItem = cartItemService.createCartItem(userId, cartItemRequestDTO);
        return new ResponseEntity<>(newCartItem, HttpStatus.CREATED);
    }

    // Only the owner or admin can view a cart item
    @PreAuthorize("@authService.canAccessCartItem(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<CartItemResponseDTO> getCartItemById(@PathVariable Long id) {
        logger.info("Fetching cart item by ID: {}", id);
        CartItemResponseDTO cartItem = cartItemService.getCartItemById(id);
        return ResponseEntity.ok(cartItem);
    }


    @GetMapping
    public ResponseEntity<List<CartItemResponseDTO>> getAllCartItems() {
        logger.info("Fetching all cart items");
        List<CartItemResponseDTO> cartItems = cartItemService.getAllCartItems();
        logger.info("Total cart items fetched: {}", cartItems.size());
        return ResponseEntity.ok(cartItems);
    }

    // Only the owner or admin can update a cart item
    @PreAuthorize("@authService.canAccessCartItem(#id)")
    @PutMapping("/{id}")
    public ResponseEntity<CartItemResponseDTO> updateCartItem(@PathVariable Long id, @Valid @RequestBody CartItemRequestDTO cartItemRequestDTO) {
        logger.info("Updating cart item ID: {}", id);
        CartItemResponseDTO updatedCartItem = cartItemService.updateCartItem(id, cartItemRequestDTO);
        return ResponseEntity.ok(updatedCartItem);
    }

    // Only the owner or admin can delete a cart item
    @PreAuthorize("@authService.canAccessCartItem(#id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCartItem(@PathVariable Long id) {
        logger.info("Deleting cart item ID: {}", id);
        cartItemService.deleteCartItem(id);
        return ResponseEntity.ok("Cart item deleted");
    }
}

