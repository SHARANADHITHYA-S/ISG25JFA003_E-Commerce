package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.CartItemRepository;
import com.cognizant.ecommerce.dao.CartRepository;
import com.cognizant.ecommerce.dao.ProductRepository;
import com.cognizant.ecommerce.dao.UserRepository;
import com.cognizant.ecommerce.dto.cartItem.CartItemRequestDTO;
import com.cognizant.ecommerce.dto.cartItem.CartItemResponseDTO;
import com.cognizant.ecommerce.mapper.CartItemMapper;
import com.cognizant.ecommerce.model.Cart;
import com.cognizant.ecommerce.model.CartItem;
import com.cognizant.ecommerce.model.Product;
import com.cognizant.ecommerce.model.User;
import com.cognizant.ecommerce.service.CartItemService;
import com.cognizant.ecommerce.exception.ResourceNotFoundException; // <-- New Import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        Product product = productRepository.findById(cartItemRequestDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + cartItemRequestDTO.getProductId()));

        Optional<CartItem> existingCartItemOptional = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        CartItem savedCartItem;
        if (existingCartItemOptional.isPresent()) {
            CartItem existingCartItem = existingCartItemOptional.get();
            existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItemRequestDTO.getQuantity());
            savedCartItem = cartItemRepository.save(existingCartItem);
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setQuantity(cartItemRequestDTO.getQuantity());
            newCartItem.setCart(cart);
            newCartItem.setProduct(product);
            savedCartItem = cartItemRepository.save(newCartItem);
        }

        return CartItemMapper.toDto(savedCartItem);
    }

    @Override
    public CartItemResponseDTO getCartItemById(Long id) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem not found with id: " + id));
        return CartItemMapper.toDto(cartItem);
    }

    @Override
    public List<CartItemResponseDTO> getAllCartItems() {
        List<CartItem> cartItems = cartItemRepository.findAll();
        return CartItemMapper.toDtoList(cartItems);
    }

    @Override
    public CartItemResponseDTO updateCartItem(Long id, CartItemRequestDTO cartItemRequestDTO) {
        CartItem existingCartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem not found with id: " + id));

        existingCartItem.setQuantity(cartItemRequestDTO.getQuantity());

        CartItem updatedCartItem = cartItemRepository.save(existingCartItem);
        return CartItemMapper.toDto(updatedCartItem);
    }

    @Override
    public void deleteCartItem(Long id) {
        cartItemRepository.deleteById(id);
    }
}