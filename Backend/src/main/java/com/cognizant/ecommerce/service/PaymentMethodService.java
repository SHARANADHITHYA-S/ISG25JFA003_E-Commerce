package com.cognizant.ecommerce.service;

import com.cognizant.ecommerce.dto.payment.PaymentMethodRequestDTO;
import com.cognizant.ecommerce.dto.payment.PaymentMethodResponseDTO;
import java.util.List;
import java.util.Optional;

public interface PaymentMethodService {
    List<PaymentMethodResponseDTO> getPaymentMethodsByUserId(Long userId);
    Optional<PaymentMethodResponseDTO> getPaymentMethodById(Long paymentMethodId);

    // POST: Method to add a new payment method
    PaymentMethodResponseDTO addPaymentMethod(Long userId, PaymentMethodRequestDTO requestDTO);

    // PUT: Method to update an existing payment method
//    PaymentMethodResponseDTO updatePaymentMethod(Long paymentMethodId, PaymentMethodRequestDTO requestDTO);

    void deletePaymentMethod(Long paymentMethodId);

    List<PaymentMethodResponseDTO> getAllPaymentMethods();
}