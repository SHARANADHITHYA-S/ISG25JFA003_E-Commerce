package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.payment.PaymentMethodRequestDTO;
import com.cognizant.ecommerce.dto.payment.PaymentMethodResponseDTO;
import com.cognizant.ecommerce.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    // Admin endpoint must be before the generic ID endpoint
    @GetMapping("/admin")
    public ResponseEntity<List<PaymentMethodResponseDTO>> getAllPaymentMethods() {
        List<PaymentMethodResponseDTO> paymentMethods = paymentMethodService.getAllPaymentMethods();
        return ok(paymentMethods);
    }

    // Endpoint to get a single payment method by ID
    @GetMapping("/{id}")
    public ResponseEntity<Optional<PaymentMethodResponseDTO>> getPaymentMethodById(@PathVariable Long id) {
        // Service layer should now handle the 'not found' exception
        Optional<PaymentMethodResponseDTO> paymentMethod = paymentMethodService.getPaymentMethodById(id);
        return ok(paymentMethod);
    }

    // User-specific endpoint to get their payment methods
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentMethodResponseDTO>> getPaymentMethodsByUserId(@PathVariable Long userId) {
        List<PaymentMethodResponseDTO> paymentMethods = paymentMethodService.getPaymentMethodsByUserId(userId);
        return ok(paymentMethods);
    }

    // Endpoint to add a new payment method for a user
    @PostMapping("/user/{userId}")
    public ResponseEntity<PaymentMethodResponseDTO> addPaymentMethod(@PathVariable Long userId, @RequestBody PaymentMethodRequestDTO requestDTO) {
        PaymentMethodResponseDTO newPaymentMethod = paymentMethodService.addPaymentMethod(userId, requestDTO);
        return new ResponseEntity<>(newPaymentMethod, HttpStatus.CREATED);
    }

    // Endpoint to delete a payment method by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable Long id) {
        paymentMethodService.deletePaymentMethod(id);
        return ResponseEntity.noContent().build();
    }
}