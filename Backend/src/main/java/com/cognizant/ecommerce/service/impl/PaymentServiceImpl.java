//package com.cognizant.ecommerce.service.impl;
//
//import com.cognizant.ecommerce.dao.OrderRepository;
//import com.cognizant.ecommerce.dao.PaymentRepository;
//import com.cognizant.ecommerce.dto.payment.PaymentRequestDTO;
//import com.cognizant.ecommerce.dto.payment.PaymentResponseDTO;
//import com.cognizant.ecommerce.model.Order;
//import com.cognizant.ecommerce.model.Payment;
//import com.cognizant.ecommerce.service.PaymentService;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.modelmapper.ModelMapper;
//import org.springframework.stereotype.Service;
//
//import java.util.Date;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class PaymentServiceImpl implements PaymentService {
//
//    private final PaymentRepository paymentRepository;
//
//
//    private final ModelMapper modelMapper;
//
//
//    private final OrderRepository orderRepository;
//
//
//    @Override
//    public PaymentResponseDTO getPaymentById(Long paymentId){
//        Payment payment = paymentRepository.findById(paymentId)
//                .orElseThrow(()->new RuntimeException("Payment not found for payment id: "+ paymentId));
//
//        return modelMapper.map(payment, PaymentResponseDTO.class);
//    }
//
//
//    @Override
//    public PaymentResponseDTO createPayment(PaymentRequestDTO paymentRequestDTO){
//
//        Order order= orderRepository.findById(paymentRequestDTO.getOrderId())
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//
//        Payment payment = Payment.builder()
//                .amount(paymentRequestDTO.getAmount())
//                .order(order)
//                .paymentMethod(order.getPaymentMethod())
//                .status("PENDING")
//                .paid_at(new Date())
//                .build();
//
//        Payment saved= paymentRepository.save(payment);
//
//        return modelMapper.map(saved, PaymentResponseDTO.class);
//
//    }
//
//    @Override
//    public PaymentResponseDTO getPaymentByOrderId(Long orderId){
//        Order order= orderRepository.findById(orderId)
//                .orElseThrow(()->new RuntimeException("Order not found "));
//
//        return modelMapper.map(order, PaymentResponseDTO.class);
//
//
//    }
//
//    @Override
//    public PaymentResponseDTO updatePaymentStatus(Long paymentId, String status){
//        Payment payment = paymentRepository.findById(paymentId)
//                .orElseThrow(()-> new RuntimeException("Payment not found"));
//
//        payment.setStatus(status);
//
//        return modelMapper.map(payment, PaymentResponseDTO.class);
//    }
//
//
//    @Override
//    public List<PaymentResponseDTO> getAllPayments(){
//        return paymentRepository.findAll()
//                .stream()
//                .map(payment -> modelMapper.map(payment, PaymentResponseDTO.class))
//                .collect(Collectors.toList());
//    }
//
//
//
//
//
//}
