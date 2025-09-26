package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.*;
import com.cognizant.ecommerce.dto.orderItem.OrderItemRequestDTO;
import com.cognizant.ecommerce.dto.orderItem.OrderItemResponseDTO;
import com.cognizant.ecommerce.model.*;
import com.cognizant.ecommerce.service.OrderItemService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class OrderItemServiceImplTest {

    @Autowired private OrderItemService orderItemService;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private AddressRepository addressRepository;
    @Autowired private PaymentMethodRepository paymentMethodRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ModelMapper modelMapper;

    private Product testProduct;
    private Order testOrder;

    @BeforeEach
    public void setup() {
        User user = userRepository.save(User.builder()
                .name("Test User")
                .email("test@example2.com")
                .password_hash("securepass")
                .created_at(new Date())
                .role("User")
                .build());

        Address address = addressRepository.save(Address.builder()
                .address_line1("123 Main St")
                .city("Coimbatore")
                .state("TN")
                .postal_code("641001")
                .country("India")
                .user(user)
                .build());

        PaymentMethod paymentMethod = paymentMethodRepository.save(PaymentMethod.builder()
                .type("Credit Card")
                .provider("Visa")
                .user(user)
                .build());

        Category category = categoryRepository.save(Category.builder()
                .name("Games")
                .build());

        testProduct = productRepository.save(Product.builder()
                .name("Test Product")
                .image_url("oignoienwinbebv")
                .price(BigDecimal.valueOf(250))
                .description("Sample product")
                .quantity(10L)
                .category(category)
                .build());

        testOrder = orderRepository.save(Order.builder()
                .user(user)
                .address(address)
                .paymentMethod(paymentMethod)
                .totalAmount(BigDecimal.valueOf(500))
                .status("PLACED")
                .placed_at(LocalDateTime.now())
                .orderItems(null)
                .build());
    }

    @Test
    public void testAddOrderItem() {
        OrderItemRequestDTO dto = OrderItemRequestDTO.builder()
                .productId(testProduct.getId())
                .id(testOrder.getId())
                .quantity(2)
                .price(BigDecimal.valueOf(250))
                .build();

        OrderItemResponseDTO response = orderItemService.addOrderItem(dto);

        System.out.println("Order"+ response);

        assertNotNull(response);
        assertEquals(2, response.getQuantity());
        assertEquals(testProduct.getId(), response.getProductId());
    }

    @Test
    public void testGetOrderItemsByOrderId() {
        OrderItemRequestDTO dto = OrderItemRequestDTO.builder()
                .productId(testProduct.getId())
                .id(testOrder.getId())
                .quantity(1)
                .price(BigDecimal.valueOf(250))
                .build();

        orderItemService.addOrderItem(dto);

        List<OrderItemResponseDTO> items = orderItemService.getOrderItemsByOrderId(testOrder.getId());

        assertFalse(items.isEmpty());
        assertEquals(1, items.size());

    }

    @Test
    public void testUpdateOrderItem() {
        OrderItemRequestDTO dto = OrderItemRequestDTO.builder()
                .productId(testProduct.getId())
                .id(testOrder.getId())
                .quantity(1)
                .price(BigDecimal.valueOf(250))
                .build();

        OrderItemResponseDTO created = orderItemService.addOrderItem(dto);

        OrderItemRequestDTO updateDTO = OrderItemRequestDTO.builder()
                .productId(testProduct.getId())
                .quantity(3)
                .price(BigDecimal.valueOf(300))
                .build();

        OrderItemResponseDTO updated = orderItemService.updateOrderItem(created.getId(), updateDTO);

        assertEquals(3, updated.getQuantity());
        assertEquals(BigDecimal.valueOf(300), updated.getPrice());
    }

    @Test
    public void testDeleteOrderItem() {
        OrderItemRequestDTO dto = OrderItemRequestDTO.builder()
                .productId(testProduct.getId())
                .id(testOrder.getId())
                .quantity(1)
                .price(BigDecimal.valueOf(250))
                .build();

        OrderItemResponseDTO created = orderItemService.addOrderItem(dto);

        assertDoesNotThrow(() -> orderItemService.deleteOrderItem(created.getId()));
        assertFalse(orderItemRepository.existsById(created.getId()));
    }
}
