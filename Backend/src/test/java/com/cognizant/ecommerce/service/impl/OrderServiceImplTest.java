package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.*;
import com.cognizant.ecommerce.dto.cartItem.CartItemRequestDTO;
import com.cognizant.ecommerce.dto.order.OrderResponseDTO;
import com.cognizant.ecommerce.exception.ResourceNotFoundException;
import com.cognizant.ecommerce.model.*;
import com.cognizant.ecommerce.service.CartItemService;
import com.cognizant.ecommerce.service.OrderService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceImplTest {

    @Autowired private OrderService orderService;
    @Autowired private CartItemService cartItemService;
    @Autowired private UserRepository userRepository;
    @Autowired private AddressRepository addressRepository;
    @Autowired private PaymentMethodRepository paymentMethodRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private ModelMapper modelMapper;

    @PersistenceContext
    private EntityManager entityManager;

    private User testUser;
    private Address testAddress;
    private PaymentMethod testPaymentMethod;
    private Product testProduct;

    @BeforeEach
    void setup() {
        testUser = userRepository.save(User.builder()
                .name("Test User")
                .email("testuser@example.com")
                .password_hash("securepass")
                .role("User")
                .created_at(new Date())
                .build());

        testAddress = addressRepository.save(Address.builder()
                .address_line1("123 Main St")
                .city("Coimbatore")
                .state("TN")
                .postal_code("641001")
                .country("India")
                .user(testUser)
                .build());

        testPaymentMethod = paymentMethodRepository.save(PaymentMethod.builder()
                .type("Credit Card")
                .provider("Visa")
                .user(testUser)
                .build());

        Category category = categoryRepository.save(Category.builder()
                .name("Electronics_" + System.currentTimeMillis())
                .build());

        testProduct = productRepository.save(Product.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(150))
                .description("Sample product")
                .image_url("http://example.com/image.jpg")
                .isActive(true)
                .quantity(10L)
                .category(category)
                .build());

        CartItemRequestDTO cartItemRequest = new CartItemRequestDTO();
        cartItemRequest.setProductId(testProduct.getId());
        cartItemRequest.setQuantity(2);

        cartItemService.createCartItem(testUser.getId(), cartItemRequest);
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void createOrder_shouldPersistOrderAndReduceStock() {
        OrderResponseDTO order = orderService.createOrder(
                testUser.getId(),
                testAddress.getId(),
                testPaymentMethod.getId()
        );

        assertNotNull(order.getId());
        assertEquals("PENDING", order.getStatus());
        assertEquals(0, order.getTotalAmount().compareTo(BigDecimal.valueOf(300)));

        Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();
        assertEquals(8L, updatedProduct.getQuantity());

        assertTrue(cartRepository.findByUserId(testUser.getId()).isEmpty());
    }

    @Test
    void getOrderById_shouldReturnCorrectOrder() {
        OrderResponseDTO created = orderService.createOrder(
                testUser.getId(),
                testAddress.getId(),
                testPaymentMethod.getId()
        );

        OrderResponseDTO fetched = orderService.getOrderById(created.getId());

        assertEquals(created.getId(), fetched.getId());
        assertEquals("PENDING", fetched.getStatus());
    }

    @Test
    void getOrdersByUserId_shouldReturnList() {
        orderService.createOrder(testUser.getId(), testAddress.getId(), testPaymentMethod.getId());

        List<OrderResponseDTO> orders = orderService.getOrdersByUserId(testUser.getId());

        assertFalse(orders.isEmpty());
        assertEquals(testUser.getId(), orders.getFirst().getUserId());
    }

    @Test
    void getAllOrders_shouldReturnList() {
        orderService.createOrder(testUser.getId(), testAddress.getId(), testPaymentMethod.getId());

        List<OrderResponseDTO> orders = orderService.getAllOrders();

        assertFalse(orders.isEmpty());
    }

    @Test
    void updateOrderStatus_shouldChangeStatus() {
        OrderResponseDTO created = orderService.createOrder(
                testUser.getId(),
                testAddress.getId(),
                testPaymentMethod.getId()
        );

        OrderResponseDTO updated = orderService.updateOrderStatus(created.getId(), "SHIPPED");

        assertEquals("SHIPPED", updated.getStatus());
    }

    @Test
    void deleteOrder_shouldMarkAsCancelled() {
        OrderResponseDTO created = orderService.createOrder(
                testUser.getId(),
                testAddress.getId(),
                testPaymentMethod.getId()
        );

        OrderResponseDTO deleted = orderService.deleteOrder(created.getId());

        assertEquals("CANCELLED", deleted.getStatus());
    }

    @Test
    void getOrderById_shouldThrowIfNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(999L));
    }

    @Test
    void getOrdersByUserId_shouldThrowIfNoneExist() {
        User newUser = userRepository.save(User.builder()
                .name("Empty User")
                .email("empty@example.com")
                .password_hash("pass")
                .role("User")
                .created_at(new Date())
                .build());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrdersByUserId(newUser.getId()));
    }

    @Test
    void getAllOrders_shouldThrowIfNoneExist() {
        orderRepository.deleteAll();
        assertThrows(ResourceNotFoundException.class, () -> orderService.getAllOrders());
    }
}
