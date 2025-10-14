package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.OrderRepository;
import com.cognizant.ecommerce.dao.PaymentRepository;
import com.cognizant.ecommerce.dto.payment.PaymentRequestDTO;
import com.cognizant.ecommerce.dto.payment.PaymentResponseDTO;
import com.cognizant.ecommerce.exception.ResourceNotFoundException;
import com.cognizant.ecommerce.model.Order;
import com.cognizant.ecommerce.model.Payment;
import com.cognizant.ecommerce.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;
    private final OrderRepository orderRepository;

    @Override
    public PaymentResponseDTO getPaymentById(Long paymentId) {
        log.info("Fetching payment with id={}", paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    log.error("Payment not found with id={}", paymentId);
                    return new ResourceNotFoundException("Payment not found for payment id: " + paymentId);
                });
        log.debug("Payment fetched: {}", payment);
        return modelMapper.map(payment, PaymentResponseDTO.class);
    }

    @Override
    public PaymentResponseDTO createPayment(PaymentRequestDTO paymentRequestDTO) {
        log.info("Creating payment for orderId={}",
                paymentRequestDTO.getOrderId());

        Order order = orderRepository.findById(paymentRequestDTO.getOrderId())
                .orElseThrow(() -> {
                    log.error("Order not found with id={}", paymentRequestDTO.getOrderId());
                    return new ResourceNotFoundException("Order not found");
                });

        Payment payment = Payment.builder()
                .amount(order.getTotalAmount())
                .order(order)
                .paymentMethod(order.getPaymentMethod())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        Payment saved = paymentRepository.save(payment);
        log.debug("Payment saved: {}", saved);

        return modelMapper.map(saved, PaymentResponseDTO.class);
    }

    @Override
    public PaymentResponseDTO getPaymentByOrderId(Long orderId) {
        log.info("Fetching payment for orderId={}", orderId);
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> {
                    log.error("Payment not found for orderId={}", orderId);
                    return new ResourceNotFoundException("Payment not found for order id: " + orderId);
                });
        log.debug("Payment fetched for orderId={}: {}", orderId, payment);
        return modelMapper.map(payment, PaymentResponseDTO.class);
    }

    @Override
    public PaymentResponseDTO updatePaymentStatus(Long paymentId, String status) {
        log.info("Updating payment status for paymentId={} to '{}'", paymentId, status);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    log.error("Payment not found with id={}", paymentId);
                    return new ResourceNotFoundException("Payment not found");
                });

        payment.setStatus(status);
        Payment updated = paymentRepository.save(payment);
        log.debug("Payment updated: {}", updated);

        return modelMapper.map(updated, PaymentResponseDTO.class);
    }

    @Override
    public List<PaymentResponseDTO> getAllPayments() {
        log.info("Fetching all payments");
        List<Payment> paymentList = paymentRepository.findAll();
        if (paymentList.isEmpty()) {
            log.error("No payments found in the system");
            throw new ResourceNotFoundException("No payments found");
        }
        log.debug("Found {} payments", paymentList.size());
        return paymentList.stream()
                .map(payment -> modelMapper.map(payment, PaymentResponseDTO.class))
                .collect(Collectors.toList());
    }
}
