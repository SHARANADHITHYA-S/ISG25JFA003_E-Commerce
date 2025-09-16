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

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<PaymentMethodResponseDTO>> getPaymentMethodsByUserId(@PathVariable Long userId) {
        List<PaymentMethodResponseDTO> paymentMethods = paymentMethodService.getPaymentMethodsByUserId(userId);
        return ResponseEntity.ok(paymentMethods);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethodResponseDTO> getPaymentMethodById(@PathVariable Long id) {
        return paymentMethodService.getPaymentMethodById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users/{userId}")
    public ResponseEntity<PaymentMethodResponseDTO> addPaymentMethod(@PathVariable Long userId, @RequestBody PaymentMethodRequestDTO requestDTO) {
        PaymentMethodResponseDTO newPaymentMethod = paymentMethodService.addPaymentMethod(userId, requestDTO);
        return new ResponseEntity<>(newPaymentMethod, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable Long id) {
        paymentMethodService.deletePaymentMethod(id);
        return ResponseEntity.noContent().build();
    }
}