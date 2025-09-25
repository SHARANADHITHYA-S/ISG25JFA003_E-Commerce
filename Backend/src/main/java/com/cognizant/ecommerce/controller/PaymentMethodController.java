package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.payment.PaymentMethodRequestDTO;
import com.cognizant.ecommerce.dto.payment.PaymentMethodResponseDTO;
import com.cognizant.ecommerce.service.PaymentMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentMethodController.class);

    private final PaymentMethodService paymentMethodService;

    // Only owner or admin can view payment methods by user ID
    @PreAuthorize("@authService.isSelfOrAdmin(#userId)")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentMethodResponseDTO>> getPaymentMethodsByUserId(@PathVariable Long userId) {
        logger.info("Fetching payment methods for user ID: {}", userId);
        List<PaymentMethodResponseDTO> paymentMethods = paymentMethodService.getPaymentMethodsByUserId(userId);
        logger.info("Found {} payment methods for user ID: {}", paymentMethods.size(), userId);
        return ResponseEntity.ok(paymentMethods);
    }

    // Only owner or admin can view a specific payment method
    @PreAuthorize("@authService.canAccessPaymentMethod(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethodResponseDTO> getPaymentMethodById(@PathVariable Long id) {
        logger.info("Fetching payment method by ID: {}", id);
        return paymentMethodService.getPaymentMethodById(id)
                .map(method -> {
                    logger.info("Payment method found: {}", method.getCardType());
                    return ResponseEntity.ok(method);
                })
                .orElseGet(() -> {
                    logger.warn("Payment method not found for ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    // Only owner or admin can add a payment method
    @PreAuthorize("@authService.isSelfOrAdmin(#userId)")
    @PostMapping("/users/{userId}")
    public ResponseEntity<PaymentMethodResponseDTO> addPaymentMethod(@PathVariable Long userId, @Valid @RequestBody PaymentMethodRequestDTO requestDTO) {
        logger.info("Adding payment method for user ID: {} with type: {}", userId, requestDTO.getCardType());
        PaymentMethodResponseDTO newPaymentMethod = paymentMethodService.addPaymentMethod(userId, requestDTO);
        logger.info("Payment method added with ID: {}", newPaymentMethod.getPaymentMethodId());
        return new ResponseEntity<>(newPaymentMethod, HttpStatus.CREATED);
    }

    // Only owner or admin can delete a payment method
    @PreAuthorize("@authService.canAccessPaymentMethod(#id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable Long id) {
        logger.info("Deleting payment method with ID: {}", id);
        paymentMethodService.deletePaymentMethod(id);
        logger.info("Payment method deleted with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}

