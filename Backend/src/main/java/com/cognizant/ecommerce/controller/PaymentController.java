package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.payment.PaymentRequestDTO;
import com.cognizant.ecommerce.dto.payment.PaymentResponseDTO;
import com.cognizant.ecommerce.service.OrderService;
import com.cognizant.ecommerce.service.PaymentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // ✅ Lombok logging
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "BearerAuth")
@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    // Only the owner or admin can create a payment
    @PreAuthorize("@authService.isSelfOrAdmin(#dto.userId)")
    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPayment(@Valid @RequestBody PaymentRequestDTO dto) {
        log.info("Received request to create payment: {}", dto);
        PaymentResponseDTO response = paymentService.createPayment(dto);
        log.info("Payment created successfully with id={} for orderId={}", response.getId(), dto.getOrderId());
        return ResponseEntity.ok(response);
    }

    // Only the owner or admin can view a payment by ID
    @PreAuthorize("@authService.canAccessPayment(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long id) {
        log.info("Fetching payment with id={}", id);
        PaymentResponseDTO payment = paymentService.getPaymentById(id);
        log.debug("Fetched payment details: {}", payment);
        return ResponseEntity.ok(payment);
    }

    // Only the owner or admin can view a payment by order ID
    @PreAuthorize("@authService.canAccessOrder(#orderId)")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentByOrderId(@PathVariable Long orderId) {
        log.info("Fetching payment for orderId={}", orderId);
        PaymentResponseDTO payment = paymentService.getPaymentByOrderId(orderId);
        log.debug("Fetched payment details for orderId {}: {}", orderId, payment);
        return ResponseEntity.ok(payment);
    }

    // Only admins can update payment status
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{id}/status")
    public ResponseEntity<PaymentResponseDTO> updatePaymentStatus(@PathVariable Long id, @RequestParam String status) {
        log.info("Updating payment status for id={} to {}", id, status);
        PaymentResponseDTO updatedPayment = paymentService.updatePaymentStatus(id, status);
        orderService.updateOrderStatus(updatedPayment.getOrderId(), status);
        log.info("Payment status updated successfully for id={}", id);
        return ResponseEntity.ok(updatedPayment);
    }

    // Only admins can view all payments
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<List<PaymentResponseDTO>> getAllPayments() {
        log.info("Fetching all payments");
        List<PaymentResponseDTO> payments = paymentService.getAllPayments();
        log.debug("Total payments fetched: {}", payments.size());
        return ResponseEntity.ok(payments);
    }
}
