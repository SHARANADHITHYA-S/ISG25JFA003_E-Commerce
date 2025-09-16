package com.cognizant.ecommerce.service;

import com.cognizant.ecommerce.dao.*;
import com.cognizant.ecommerce.dto.order.OrderRequestDTO;
import com.cognizant.ecommerce.dto.order.OrderResponseDTO;
import com.cognizant.ecommerce.dto.orderItem.OrderItemRequestDTO;
import com.cognizant.ecommerce.model.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
public class OrderServiceImplTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;


    private User testUser;

    private Order testOrder;

    private Address testAddress;

    private PaymentMethod testPaymentMethod;

    private Product testProduct;

    private OrderRequestDTO orderRequestDTO;

    @BeforeEach
    public void setup() {
        testUser = userRepository.save(User.builder()
                .name("Test User")
                .email("test@example3.com")
                .password_hash("securepass")
                .created_at(new Date())
                .role("User")
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
                .name("footwear")
                .build());

        testProduct = productRepository.save(Product.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(250))
                .description("Sample product")
                .image_url("http://bruh.com")
                .category(category)
                .build());
    }

    @Test
    public void testCreateOrder() {
        OrderItemRequestDTO itemDTO = OrderItemRequestDTO.builder()
                .productId(testProduct.getId())
                .quantity(2)
                .price(BigDecimal.valueOf(250))
                .build();

        OrderRequestDTO orderRequest = OrderRequestDTO.builder()
                .userid(testUser.getId())
                .addressId(testAddress.getId())
                .paymentMethodId(testPaymentMethod.getId())
                .status("PLACED")
                .totalAmount(BigDecimal.valueOf(500))
                .items(List.of(itemDTO))
                .build();

        OrderResponseDTO response = orderService.createOrder(orderRequest);

        assertNotNull(response);
        assertEquals("PLACED", response.getStatus());
        assertEquals(testUser.getId(), response.getUserId());
        assertEquals(1, response.getOrderItems().size());
        assertEquals(testProduct.getId(), response.getOrderItems().getFirst().getProductId());
    }

    @Test
    public void testGetOrderById() {
        // First, create and save an order
        OrderItemRequestDTO itemDTO = OrderItemRequestDTO.builder()
                .productId(testProduct.getId())
                .quantity(2)
                .price(BigDecimal.valueOf(250))
                .build();

        OrderRequestDTO orderRequest = OrderRequestDTO.builder()
                .userid(testUser.getId())
                .addressId(testAddress.getId())
                .paymentMethodId(testPaymentMethod.getId())
                .totalAmount(BigDecimal.valueOf(500))
                .items(List.of(itemDTO))
                .build();

        OrderResponseDTO createdOrder = orderService.createOrder(orderRequest);

        // Now test retrieval
        OrderResponseDTO response = orderService.getOrderById(createdOrder.getId());

        assertNotNull(response);
        assertEquals(createdOrder.getId(), response.getId());
        assertEquals("PLACED", response.getStatus());
    }

    @Test
    public void testUpdateOrderStatus() {
        // Create and save an order
        OrderItemRequestDTO itemDTO = OrderItemRequestDTO.builder()
                .productId(testProduct.getId())
                .quantity(2)
                .price(BigDecimal.valueOf(250))
                .build();

        OrderRequestDTO orderRequest = OrderRequestDTO.builder()
                .userid(testUser.getId())
                .status("PLACED")
                .addressId(testAddress.getId())
                .paymentMethodId(testPaymentMethod.getId())
                .totalAmount(BigDecimal.valueOf(500))
                .items(List.of(itemDTO))
                .build();

        OrderResponseDTO createdOrder = orderService.createOrder(orderRequest);

        // Update status
        OrderResponseDTO updatedOrder = orderService.updateOrderStatus(createdOrder.getId(), "SHIPPED");

        assertNotNull(updatedOrder);
        assertEquals("SHIPPED", updatedOrder.getStatus());
    }

    @Test
    public void testDeleteOrder() {
        // Create and save an order
        OrderItemRequestDTO itemDTO = OrderItemRequestDTO.builder()
                .productId(testProduct.getId())
                .quantity(2)
                .price(BigDecimal.valueOf(250))
                .build();

        OrderRequestDTO orderRequest = OrderRequestDTO.builder()
                .userid(testUser.getId())
                .addressId(testAddress.getId())
                .paymentMethodId(testPaymentMethod.getId())
                .totalAmount(BigDecimal.valueOf(500))
                .items(List.of(itemDTO))
                .build();

        OrderResponseDTO createdOrder = orderService.createOrder(orderRequest);

        // Delete the order
        assertDoesNotThrow(() -> orderService.deleteOrder(createdOrder.getId()));

        // Confirm deletion
        assertThrows(RuntimeException.class, () -> orderService.getOrderById(createdOrder.getId()));
    }
}
