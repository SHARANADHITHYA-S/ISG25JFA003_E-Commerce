package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.CartItemRepository;
import com.cognizant.ecommerce.dao.CartRepository;
import com.cognizant.ecommerce.dao.ProductRepository;
import com.cognizant.ecommerce.dao.UserRepository;
import com.cognizant.ecommerce.dto.cart.CartResponseDTO;
import com.cognizant.ecommerce.dto.cartItem.CartItemResponseDTO;
import com.cognizant.ecommerce.exception.ResourceNotFoundException;
import com.cognizant.ecommerce.model.Cart;
import com.cognizant.ecommerce.model.CartItem;
import com.cognizant.ecommerce.model.Product;
import com.cognizant.ecommerce.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.*;

import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CartServiceImplTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;

    private Cart cart;
    private User user;
    private Product product;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);

        product = new Product();
        product.setId(10L);
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(100));

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setCreated_at(LocalDateTime.now());
        cart.setUpdated_at(LocalDateTime.now());

        cartItem = new CartItem();
        cartItem.setId(100L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);
        cart.setCartItems(cartItems);
    }

    @Test
    void testGetCartByUserId_Success() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        CartResponseDTO response = cartService.getCartByUserId(1L);

        assertNotNull(response);
        assertEquals(cart.getId(), response.getId());
        assertEquals(user.getId(), response.getUserId());
        assertEquals(1, response.getItems().size());

        CartItemResponseDTO itemDto = response.getItems().get(0);
        assertEquals(cartItem.getId(), itemDto.getId());
        assertEquals(product.getId(), itemDto.getProductId());
        assertEquals(product.getName(), itemDto.getProductName());
        assertEquals(product.getPrice(), itemDto.getPrice());
        assertEquals(cartItem.getQuantity(), itemDto.getQuantity());
        assertEquals(BigDecimal.valueOf(200), itemDto.getTotalPrice());
        assertEquals(BigDecimal.valueOf(200), response.getTotalPrice());
    }

    @Test
    void testGetCartByUserId_CartNotFound() {
        when(cartRepository.findByUserId(2L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            cartService.getCartByUserId(2L);
        });
        assertTrue(exception.getMessage().contains("Cart not found for user with ID: 2"));
    }

    @Test
    void testDeleteCartByUserId_Success() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        doNothing().when(cartRepository).deleteByUserId(1L);

        assertDoesNotThrow(() -> cartService.deleteCartByUserId(1L));
        verify(cartRepository, times(1)).deleteByUserId(1L);
    }

    @Test
    void testDeleteCartByUserId_CartNotFound() {
        when(cartRepository.findByUserId(2L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> cartService.deleteCartByUserId(2L));
    }
}