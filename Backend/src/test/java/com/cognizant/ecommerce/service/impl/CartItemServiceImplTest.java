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
import com.cognizant.ecommerce.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CartItemServiceImplTest {

    @InjectMocks
    private CartItemServiceImpl cartItemService;

    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;

    private User user;
    private Product product;
    private Cart cart;
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
        product.setQuantity(10L);
        product.setActive(true);

        cart = new Cart();
        cart.setId(5L);
        cart.setUser(user);

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
    void testCreateCartItem_NewItem_Success() {
        CartItemRequestDTO dto = new CartItemRequestDTO();
        dto.setProductId(product.getId());
        dto.setQuantity(2);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> {
            CartItem saved = i.getArgument(0);
            saved.setId(999L);
            return saved;
        });

        // Remove the item from cart to simulate "new" product in cart
        cart.setCartItems(new ArrayList<>());
        CartItemResponseDTO response = cartItemService.createCartItem(user.getId(), dto);

        assertNotNull(response);
        assertEquals(product.getId(), response.getProductId());
        assertEquals(2, response.getQuantity());
        assertEquals(BigDecimal.valueOf(200), response.getTotalPrice());
    }

    @Test
    void testCreateCartItem_ExistingItem_UpdatesQuantity() {
        CartItemRequestDTO dto = new CartItemRequestDTO();
        dto.setProductId(product.getId());
        dto.setQuantity(3);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArgument(0));

        CartItemResponseDTO response = cartItemService.createCartItem(user.getId(), dto);

        assertNotNull(response);
        assertEquals(product.getId(), response.getProductId());
        assertEquals(5, response.getQuantity()); // 2 existing + 3 new
        assertEquals(BigDecimal.valueOf(500), response.getTotalPrice());
    }

    @Test
    void testCreateCartItem_ProductNotFound() {
        CartItemRequestDTO dto = new CartItemRequestDTO();
        dto.setProductId(99L);
        dto.setQuantity(1);

        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            cartItemService.createCartItem(user.getId(), dto);
        });
    }

    @Test
    void testCreateCartItem_ProductNotActive() {
        CartItemRequestDTO dto = new CartItemRequestDTO();
        dto.setProductId(product.getId());
        dto.setQuantity(1);

        product.setActive(false);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        assertThrows(ResourceNotFoundException.class, () -> {
            cartItemService.createCartItem(user.getId(), dto);
        });
    }

    @Test
    void testCreateCartItem_InsufficientStock() {
        CartItemRequestDTO dto = new CartItemRequestDTO();
        dto.setProductId(product.getId());
        dto.setQuantity(20);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));

        assertThrows(ResourceNotFoundException.class, () -> {
            cartItemService.createCartItem(user.getId(), dto);
        });
    }

    @Test
    void testGetCartItemById_Success() {
        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.of(cartItem));

        CartItemResponseDTO response = cartItemService.getCartItemById(cartItem.getId());

        assertNotNull(response);
        assertEquals(cartItem.getId(), response.getId());
        assertEquals(product.getId(), response.getProductId());
    }

    @Test
    void testGetCartItemById_NotFound() {
        when(cartItemRepository.findById(888L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            cartItemService.getCartItemById(888L);
        });
    }

    @Test
    void testGetAllCartItems() {
        when(cartItemRepository.findAll()).thenReturn(List.of(cartItem));

        List<CartItemResponseDTO> result = cartItemService.getAllCartItems();

        assertEquals(1, result.size());
        assertEquals(cartItem.getId(), result.get(0).getId());
    }

    @Test
    void testUpdateCartItem_Success() {
        CartItemRequestDTO dto = new CartItemRequestDTO();
        dto.setQuantity(4);

        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArgument(0));

        CartItemResponseDTO response = cartItemService.updateCartItem(cartItem.getId(), dto);

        assertNotNull(response);
        assertEquals(4, response.getQuantity());
        assertEquals(BigDecimal.valueOf(400), response.getTotalPrice());
    }

    @Test
    void testUpdateCartItem_InsufficientStock() {
        CartItemRequestDTO dto = new CartItemRequestDTO();
        dto.setQuantity(100);

        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.of(cartItem));

        assertThrows(ResourceNotFoundException.class, () -> {
            cartItemService.updateCartItem(cartItem.getId(), dto);
        });
    }

    @Test
    void testUpdateCartItem_NotFound() {
        CartItemRequestDTO dto = new CartItemRequestDTO();
        dto.setQuantity(1);

        when(cartItemRepository.findById(888L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            cartItemService.updateCartItem(888L, dto);
        });
    }

    @Test
    void testDeleteCartItem_Success() {
        when(cartItemRepository.existsById(cartItem.getId())).thenReturn(true);
        doNothing().when(cartItemRepository).deleteById(cartItem.getId());

        assertDoesNotThrow(() -> cartItemService.deleteCartItem(cartItem.getId()));
        verify(cartItemRepository, times(1)).deleteById(cartItem.getId());
    }

    @Test
    void testDeleteCartItem_NotFound() {
        when(cartItemRepository.existsById(888L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            cartItemService.deleteCartItem(888L);
        });
    }
}