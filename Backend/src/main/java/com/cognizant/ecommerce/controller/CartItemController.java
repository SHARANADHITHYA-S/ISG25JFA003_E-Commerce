package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.cartItem.CartItemRequestDTO;
import com.cognizant.ecommerce.dto.cartItem.CartItemResponseDTO;
import com.cognizant.ecommerce.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cart-items")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    @PostMapping("/{userId}")
    public ResponseEntity<CartItemResponseDTO> createCartItem(@PathVariable Long userId, @RequestBody CartItemRequestDTO cartItemRequestDTO) {
        CartItemResponseDTO newCartItem = cartItemService.createCartItem(userId, cartItemRequestDTO);
        return new ResponseEntity<>(newCartItem, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartItemResponseDTO> getCartItemById(@PathVariable Long id) {
        CartItemResponseDTO cartItem = cartItemService.getCartItemById(id);
        return ResponseEntity.ok(cartItem);
    }

    @GetMapping
    public ResponseEntity<List<CartItemResponseDTO>> getAllCartItems() {
        List<CartItemResponseDTO> cartItems = cartItemService.getAllCartItems();
        return ResponseEntity.ok(cartItems);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartItemResponseDTO> updateCartItem(@PathVariable Long id, @RequestBody CartItemRequestDTO cartItemRequestDTO) {
        CartItemResponseDTO updatedCartItem = cartItemService.updateCartItem(id, cartItemRequestDTO);
        return ResponseEntity.ok(updatedCartItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long id) {
        cartItemService.deleteCartItem(id);
        return ResponseEntity.noContent().build();
    }
}