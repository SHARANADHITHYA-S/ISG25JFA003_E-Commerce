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
import com.cognizant.ecommerce.exception.ResourceNotFoundException;
import com.cognizant.ecommerce.exception.PaymentMethodNotFoundException; // Ensure this import exists

import java.time.LocalDateTime;
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
        // Find by ID and throw a specific exception if not found
        return paymentMethodRepository.findById(id)
                .map(this::mapToResponseDTO)
                .or(() -> {
                    throw new PaymentMethodNotFoundException("Payment method not found with ID: " + id);
                });
    }

    @Override
    public PaymentMethodResponseDTO addPaymentMethod(Long userId, PaymentMethodRequestDTO requestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setUser(user);
        paymentMethod.setType(requestDTO.getCardType());
        paymentMethod.setProvider("Credit Card");
        paymentMethod.setAccount_number(requestDTO.getCardNumber());
        paymentMethod.setCardholderName(requestDTO.getCardholderName());
        paymentMethod.setExpiry_date(requestDTO.getExpirationDate());
        paymentMethod.setIs_default(requestDTO.isDefault());
        paymentMethod.setCreated_at(LocalDateTime.now());
        paymentMethod.setUpdated_at(LocalDateTime.now());

        PaymentMethod savedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        return mapToResponseDTO(savedPaymentMethod);
    }

    @Override
    public void deletePaymentMethod(Long id) {
        // Check if the payment method exists before attempting to delete
        paymentMethodRepository.findById(id)
                .orElseThrow(() -> new PaymentMethodNotFoundException("Payment method not found with ID: " + id));
        paymentMethodRepository.deleteById(id);
    }

    @Override
    public List<PaymentMethodResponseDTO> getAllPaymentMethods() {
        return paymentMethodRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private PaymentMethodResponseDTO mapToResponseDTO(PaymentMethod paymentMethod) {
        PaymentMethodResponseDTO responseDTO = new PaymentMethodResponseDTO();
        responseDTO.setPaymentMethodId(paymentMethod.getId());
        responseDTO.setCardType(paymentMethod.getType());

        // FIX: Added a null and length check to prevent NullPointerException
        String accountNumber = paymentMethod.getAccount_number();
        if (accountNumber != null && accountNumber.length() >= 4) {
            responseDTO.setLastFourDigits(accountNumber.substring(accountNumber.length() - 4));
        } else {
            responseDTO.setLastFourDigits("N/A"); // Default value for incomplete data
        }

        responseDTO.setCardholderName(paymentMethod.getCardholderName());

        return responseDTO;
    }
}