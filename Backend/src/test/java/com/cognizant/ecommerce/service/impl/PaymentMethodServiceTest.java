package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.PaymentMethodRepository;
import com.cognizant.ecommerce.dao.UserRepository;
import com.cognizant.ecommerce.dto.payment.PaymentMethodRequestDTO;
import com.cognizant.ecommerce.dto.payment.PaymentMethodResponseDTO;
import com.cognizant.ecommerce.exception.PaymentMethodNotFoundException;
import com.cognizant.ecommerce.exception.ResourceNotFoundException;
import com.cognizant.ecommerce.model.PaymentMethod;
import com.cognizant.ecommerce.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentMethodServiceTest {

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PaymentMethodServiceImpl paymentMethodService;

    // Helper method to create a sample User
    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    // Helper method to create a sample PaymentMethod entity
    private PaymentMethod createPaymentMethod(Long id) {
        PaymentMethod pm = new PaymentMethod();
        pm.setId(id);
        pm.setUser(createUser(1L));
        pm.setType("Credit Card");
        pm.setAccount_number("1234567812345678");
        pm.setCreated_at(LocalDateTime.now());
        pm.setIs_default(true);
        return pm;
    }

    // Helper method to create a sample DTO for requests
    private PaymentMethodRequestDTO createRequestDTO() {
        PaymentMethodRequestDTO dto = new PaymentMethodRequestDTO();
        dto.setCardType("Credit Card");
        dto.setCardNumber("1234567812345678");
        dto.setExpirationDate("12/26");
        dto.setDefault(true);
        return dto;
    }

    @Test
    void testGetAllPaymentMethods_Success() {
        PaymentMethod pm = createPaymentMethod(1L);
        List<PaymentMethod> paymentMethods = Collections.singletonList(pm);

        when(paymentMethodRepository.findAll()).thenReturn(paymentMethods);

        List<PaymentMethodResponseDTO> result = paymentMethodService.getAllPaymentMethods();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getPaymentMethodId());
    }

    @Test
    void testGetPaymentMethodsByUserId_Success() {
        PaymentMethod pm = createPaymentMethod(1L);
        List<PaymentMethod> paymentMethods = Collections.singletonList(pm);

        when(paymentMethodRepository.findByUserId(anyLong())).thenReturn(paymentMethods);

        List<PaymentMethodResponseDTO> result = paymentMethodService.getPaymentMethodsByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getPaymentMethodId());
        verify(paymentMethodRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testGetPaymentMethodById_Success() {
        PaymentMethod pm = createPaymentMethod(1L);

        when(paymentMethodRepository.findById(anyLong())).thenReturn(Optional.of(pm));

        Optional<PaymentMethodResponseDTO> result = paymentMethodService.getPaymentMethodById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getPaymentMethodId());
    }

    @Test
    void testGetPaymentMethodById_NotFound() {
        when(paymentMethodRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PaymentMethodNotFoundException.class, () -> paymentMethodService.getPaymentMethodById(99L));
    }


    @Test
    void testAddPaymentMethod_Success() {
        User user = createUser(1L);
        PaymentMethodRequestDTO requestDTO = createRequestDTO();
        PaymentMethod savedPm = createPaymentMethod(1L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(savedPm);

        PaymentMethodResponseDTO result = paymentMethodService.addPaymentMethod(1L, requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getPaymentMethodId());
        verify(userRepository, times(1)).findById(1L);
        verify(paymentMethodRepository, times(1)).save(any(PaymentMethod.class));
    }

    @Test
    void testAddPaymentMethod_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentMethodService.addPaymentMethod(1L, createRequestDTO()));
        verify(paymentMethodRepository, never()).save(any(PaymentMethod.class));
    }

    @Test
    void testDeletePaymentMethod_Success() {
        PaymentMethod pm = createPaymentMethod(1L);

        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(pm));
        doNothing().when(paymentMethodRepository).deleteById(1L);

        assertDoesNotThrow(() -> paymentMethodService.deletePaymentMethod(1L));

        verify(paymentMethodRepository, times(1)).findById(1L);
        verify(paymentMethodRepository, times(1)).deleteById(1L);
    }

}