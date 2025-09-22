package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.payment.PaymentMethodRequestDTO;
import com.cognizant.ecommerce.dto.payment.PaymentMethodResponseDTO;
import com.cognizant.ecommerce.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    // GET all payment methods
    @GetMapping
    public ResponseEntity<List<PaymentMethodResponseDTO>> getAllPaymentMethods() {
        List<PaymentMethodResponseDTO> paymentMethods = paymentMethodService.getAllPaymentMethods();
        return ResponseEntity.ok(paymentMethods);
    }

    // GET payment methods by user ID
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<PaymentMethodResponseDTO>> getPaymentMethodsByUserId(@PathVariable Long userId) {
        List<PaymentMethodResponseDTO> paymentMethods = paymentMethodService.getPaymentMethodsByUserId(userId);
        return ResponseEntity.ok(paymentMethods);
    }

    // GET a single payment method by ID
    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethodResponseDTO> getPaymentMethodById(@PathVariable Long id) {
        return paymentMethodService.getPaymentMethodById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Add a new payment method for a user
    @PostMapping("/users/{userId}")
    public ResponseEntity<PaymentMethodResponseDTO> addPaymentMethod(@PathVariable Long userId, @RequestBody PaymentMethodRequestDTO requestDTO) {
        PaymentMethodResponseDTO newPaymentMethod = paymentMethodService.addPaymentMethod(userId, requestDTO);
        return new ResponseEntity<>(newPaymentMethod, HttpStatus.CREATED);
    }

    // Delete a payment method by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable Long id) {
        paymentMethodService.deletePaymentMethod(id);
        return ResponseEntity.noContent().build();
    }
}