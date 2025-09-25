package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.*;
import com.cognizant.ecommerce.dto.payment.PaymentRequestDTO;
import com.cognizant.ecommerce.dto.payment.PaymentResponseDTO;
import com.cognizant.ecommerce.model.*;
import com.cognizant.ecommerce.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class PaymentServiceImplTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    private Order testOrder;

    private PaymentMethod testPaymentMethod;

    @BeforeEach
    public void setup() {

        User testUser = User.builder()
                .name("Test User")
                .email("testuser@example.com")
                .password_hash("securepassword")
                .created_at(new Date())
                .role("User")
                .build();
        userRepository.save(testUser);

        // Create and save a test address
        Address testAddress = Address.builder()
                .address_line1("123 Main Street")
                .city("Coimbatore")
                .state("Tamil Nadu")
                .postal_code("641001")
                .country("India")
                .user(testUser) // assuming Address has a user field
                .build();
        addressRepository.save(testAddress);

        // Create and save a test payment method
        testPaymentMethod = PaymentMethod.builder()
                .type("Credit Card")
                .provider("Visa")
                .user(testUser) // assuming PaymentMethod has a user field
                .build();
        paymentMethodRepository.save(testPaymentMethod);

        // Create and save a test order
        testOrder = orderRepository.save(Order.builder()
                .user(testUser)
                .address(testAddress)
                .paymentMethod(testPaymentMethod)
                .status("PENDING")
                .totalAmount(BigDecimal.valueOf(1000))
                .placed_at(LocalDateTime.now())
                .build());

    }

    @Test
    public void testCreatePayment() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setOrderId(testOrder.getId());
        dto.setAmount(BigDecimal.valueOf(1000));


        PaymentResponseDTO response = paymentService.createPayment(dto);

        assertNotNull(response);
        assertEquals("PENDING", response.getStatus());
        assertEquals(testOrder.getId(), response.getOrderId());
        System.out.println("Saving payment with ID: " + response.getId());

    }

    @Test
    public void testGetAllPayments() {
        Payment payment = new Payment();
        payment.setOrder(testOrder);
        payment.setPaymentMethod(testPaymentMethod);
        payment.setAmount(BigDecimal.valueOf(1000));
        payment.setStatus("PENDING");

        paymentRepository.save(payment);

        List<PaymentResponseDTO> payments = paymentService.getAllPayments();
        System.out.println(payments);
        assertFalse(payments.isEmpty());
    }

    @Test
    public void testUpdatePaymentStatus() {
        Payment payment = new Payment();
        payment.setOrder(testOrder);
        payment.setPaymentMethod(testPaymentMethod);
        payment.setAmount(BigDecimal.valueOf(1000));
        payment.setStatus("COMPLETED");

        payment = paymentRepository.save(payment);

        PaymentResponseDTO updated = paymentService.updatePaymentStatus(payment.getId(), "COMPLETED");

        System.out.println(updated.getStatus());

        assertEquals("COMPLETED", updated.getStatus());
    }

    @Test
    public void testGetPaymentById() {
        Payment payment = new Payment();
        payment.setOrder(testOrder);
        payment.setPaymentMethod(testPaymentMethod);
        payment.setAmount(BigDecimal.valueOf(1000));
        payment.setStatus("PENDING");
        
        payment = paymentRepository.save(payment);

        PaymentResponseDTO response = paymentService.getPaymentById(payment.getId());

        assertEquals(payment.getId(), response.getId());
        assertEquals("PENDING", response.getStatus());
    }
}
