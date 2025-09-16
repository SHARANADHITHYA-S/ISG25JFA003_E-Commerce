package com.cognizant.ecommerce.service;

import com.cognizant.ecommerce.dto.payment.PaymentRequestDTO;
import com.cognizant.ecommerce.dto.payment.PaymentResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;


public interface PaymentService {

    //create a new payment for an order
    PaymentResponseDTO createPayment(PaymentRequestDTO paymentRequestDTO);


    //retrieve a payment by its ID
    PaymentResponseDTO getPaymentById(Long paymentId);


    //Retrieve payments for a given order
    PaymentResponseDTO getPaymentByOrderId(Long orderId);

    //Update Payment Status
    PaymentResponseDTO updatePaymentStatus(Long paymentId, String status);


    //Retrieve all payments in the system
     List<PaymentResponseDTO> getAllPayments();
}
