package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.CartItemRepository;
import com.cognizant.ecommerce.dao.CartRepository;
import com.cognizant.ecommerce.dao.UserRepository;
import com.cognizant.ecommerce.dto.cart.CartResponseDTO;
import com.cognizant.ecommerce.exception.ResourceNotFoundException;
import com.cognizant.ecommerce.model.Cart;
import com.cognizant.ecommerce.model.CartItem;
import com.cognizant.ecommerce.model.Product;
import com.cognizant.ecommerce.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceImplTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCartByUserId_success() {
        Long userId = 1L;
        Product product = new Product();
        product.setId(101L);
        product.setName("Laptop");
        product.setPrice(BigDecimal.valueOf(1000));

        CartItem item = new CartItem();
        item.setId(201L);
        item.setProduct(product);
        item.setQuantity(2);

        Cart cart = new Cart();
        cart.setId(301L);
        cart.setUser(new User());
        cart.setCartItems(List.of(item));

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        CartResponseDTO response = cartService.getCartByUserId(userId);

        assertEquals(301L, response.getId());
        assertEquals(1, response.getItems().size());
        assertEquals(BigDecimal.valueOf(2000), response.getItems().get(0).getTotalPrice());
    }

    @Test
    void testGetCartByUserId_notFound() {
        Long userId = 2L;
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.getCartByUserId(userId));
    }

    @Test
    void testDeleteCartByUserId_success() {
        Long userId = 3L;
        Cart cart = new Cart();
        cart.setId(401L);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        cartService.deleteCartByUserId(userId);

        verify(cartRepository).delete(cart);
    }

    @Test
    void testDeleteCartByUserId_notFound() {
        Long userId = 4L;
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.deleteCartByUserId(userId));
    }

    @Test
    void testClearCart_success() {
        Long userId = 5L;
        CartItem item1 = new CartItem();
        CartItem item2 = new CartItem();

        Cart cart = new Cart();
        cart.setId(501L);
        cart.setCartItems(List.of(item1, item2));

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        cartService.clearCart(userId);

        verify(cartItemRepository).deleteAll(cart.getCartItems());
    }

    @Test
    void testClearCart_notFound() {
        Long userId = 6L;
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.clearCart(userId));
    }
}