package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.CartItemRepository;
import com.cognizant.ecommerce.dao.CartRepository;
import com.cognizant.ecommerce.dao.ProductRepository;
import com.cognizant.ecommerce.dao.UserRepository;
import com.cognizant.ecommerce.dto.cartItem.CartItemRequestDTO;
import com.cognizant.ecommerce.dto.cartItem.CartItemResponseDTO;
import com.cognizant.ecommerce.exception.ResourceNotFoundException;
import com.cognizant.ecommerce.model.Cart;
import com.cognizant.ecommerce.model.CartItem;
import com.cognizant.ecommerce.model.Product;
import com.cognizant.ecommerce.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartItemServiceImpl implements CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public CartItemResponseDTO createCartItem(Long userId, CartItemRequestDTO cartItemRequestDTO) {

        // Step 1: Find the product and perform initial checks
        Product product = productRepository.findById(cartItemRequestDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + cartItemRequestDTO.getProductId()));

        if (!product.isActive()) {
            throw new ResourceNotFoundException("Product '" + product.getName() + "' is not active and cannot be added to cart.");
        }

        // Step 2: Find or create the user's cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId)));
                    return cartRepository.save(newCart);
                });

        // Step 3: Check if the product already exists in the cart and update quantity
        return cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .map(existingItem -> {
                    int newQuantity = existingItem.getQuantity() + cartItemRequestDTO.getQuantity();
                    if (newQuantity > product.getQuantity()) {
                        throw new ResourceNotFoundException("Insufficient stock for product '" + product.getName() + "'. Available stock: " + product.getQuantity());
                    }
                    existingItem.setQuantity(newQuantity);
                    return convertToCartItemResponseDTO(cartItemRepository.save(existingItem));
                })
                .orElseGet(() -> {
                    // Step 4: If product is not in cart, add a new cart item
                    if (cartItemRequestDTO.getQuantity() > product.getQuantity()) {
                        throw new ResourceNotFoundException("Insufficient stock for product '" + product.getName() + "'. Available stock: " + product.getQuantity());
                    }
                    CartItem newCartItem = new CartItem();
                    newCartItem.setCart(cart);
                    newCartItem.setProduct(product);
                    newCartItem.setQuantity(cartItemRequestDTO.getQuantity());
                    return convertToCartItemResponseDTO(cartItemRepository.save(newCartItem));
                });
    }

    @Override
    public CartItemResponseDTO getCartItemById(Long id) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem not found with ID: " + id));
        return convertToCartItemResponseDTO(cartItem);
    }

    @Override
    public List<CartItemResponseDTO> getAllCartItems() {
        return cartItemRepository.findAll().stream()
                .map(this::convertToCartItemResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CartItemResponseDTO updateCartItem(Long id, CartItemRequestDTO cartItemRequestDTO) {
        CartItem existingCartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem not found with ID: " + id));

        Product product = existingCartItem.getProduct();
        if (cartItemRequestDTO.getQuantity() > product.getQuantity()) {
            throw new ResourceNotFoundException("Insufficient stock for product '" + product.getName() + "'. Available stock: " + product.getQuantity());
        }

        // Update quantity
        existingCartItem.setQuantity(cartItemRequestDTO.getQuantity());

        return convertToCartItemResponseDTO(cartItemRepository.save(existingCartItem));
    }

    @Override
    @Transactional
    public void deleteCartItem(Long id) {
        if (!cartItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("CartItem not found with ID: " + id);
        }
        cartItemRepository.deleteById(id);
    }

    private CartItemResponseDTO convertToCartItemResponseDTO(CartItem cartItem) {
        Product product = cartItem.getProduct();
        CartItemResponseDTO dto = new CartItemResponseDTO();
        dto.setId(cartItem.getId());
        dto.setCartId(cartItem.getCart().getId());
        dto.setUserId(cartItem.getCart().getUser().getId());
        dto.setProductId(product.getId());
        dto.setProductName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setQuantity(cartItem.getQuantity());
        dto.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        return dto;
    }
}