package com.cognizant.ecommerce.service;

import com.cognizant.ecommerce.dto.payment.PaymentMethodRequestDTO;
import com.cognizant.ecommerce.dto.payment.PaymentMethodResponseDTO;
import java.util.List;
import java.util.Optional;

public interface PaymentMethodService {
    List<PaymentMethodResponseDTO> getPaymentMethodsByUserId(Long userId);
    Optional<PaymentMethodResponseDTO> getPaymentMethodById(Long paymentMethodId);
    PaymentMethodResponseDTO addPaymentMethod(Long userId, PaymentMethodRequestDTO requestDTO);
    void deletePaymentMethod(Long paymentMethodId);
}