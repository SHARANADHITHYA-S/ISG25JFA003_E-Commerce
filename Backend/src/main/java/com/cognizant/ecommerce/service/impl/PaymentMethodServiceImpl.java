package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.PaymentMethodRepository;
import com.cognizant.ecommerce.dao.UserRepository;
import com.cognizant.ecommerce.dto.payment.PaymentMethodRequestDTO;
import com.cognizant.ecommerce.dto.payment.PaymentMethodResponseDTO;
import com.cognizant.ecommerce.model.PaymentMethod;
import com.cognizant.ecommerce.model.User;
import com.cognizant.ecommerce.service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime; // Use the modern date/time class
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;

    @Autowired
    public PaymentMethodServiceImpl(PaymentMethodRepository paymentMethodRepository, UserRepository userRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<PaymentMethodResponseDTO> getPaymentMethodsByUserId(Long userId) {
        return paymentMethodRepository.findByUserId(userId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PaymentMethodResponseDTO> getPaymentMethodById(Long id) {
        return paymentMethodRepository.findById(id).map(this::mapToResponseDTO);
    }

    @Override
    public PaymentMethodResponseDTO addPaymentMethod(Long userId, PaymentMethodRequestDTO requestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setUser(user);
        paymentMethod.setType(requestDTO.getCardType());
        paymentMethod.setProvider("Credit Card");
        paymentMethod.setAccount_number(requestDTO.getCardNumber());
        paymentMethod.setExpiry_date(requestDTO.getExpirationDate());
        paymentMethod.setIs_default(requestDTO.isDefault());

        // Timestamps are now handled automatically by the annotations
        // No manual setting is needed here.

        PaymentMethod savedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        return mapToResponseDTO(savedPaymentMethod);
    }

    @Override
    public PaymentMethodResponseDTO updatePaymentMethod(Long paymentMethodId, PaymentMethodRequestDTO requestDTO) {
        PaymentMethod existingPaymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new RuntimeException("Payment method not found"));

        existingPaymentMethod.setType(requestDTO.getCardType());
        existingPaymentMethod.setAccount_number(requestDTO.getCardNumber());
        existingPaymentMethod.setExpiry_date(requestDTO.getExpirationDate());
        existingPaymentMethod.setIs_default(requestDTO.isDefault());

        // The updated_at timestamp is now handled automatically by the annotation

        PaymentMethod updatedPaymentMethod = paymentMethodRepository.save(existingPaymentMethod);
        return mapToResponseDTO(updatedPaymentMethod);
    }

    @Override
    public void deletePaymentMethod(Long id) {
        paymentMethodRepository.deleteById(id);
    }

    private PaymentMethodResponseDTO mapToResponseDTO(PaymentMethod paymentMethod) {
        PaymentMethodResponseDTO responseDTO = new PaymentMethodResponseDTO();
        responseDTO.setPaymentMethodId(paymentMethod.getId());
        responseDTO.setCardType(paymentMethod.getType());

        // Corrected logic with a null check to avoid NullPointerException
        String accountNumber = paymentMethod.getAccount_number();
        if (accountNumber != null && accountNumber.length() >= 4) {
            responseDTO.setLastFourDigits(accountNumber.substring(accountNumber.length() - 4));
        } else {
            responseDTO.setLastFourDigits(null);
        }

        responseDTO.setCardholderName(null);
        responseDTO.setDefault(paymentMethod.isIs_default());
        return responseDTO;
    }
}