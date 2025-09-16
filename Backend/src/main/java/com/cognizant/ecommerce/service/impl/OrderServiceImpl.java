package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.*;
import com.cognizant.ecommerce.dto.order.OrderRequestDTO;
import com.cognizant.ecommerce.dto.order.OrderResponseDTO;
import com.cognizant.ecommerce.exception.ResourceNotFoundException;
import com.cognizant.ecommerce.model.*;
import com.cognizant.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ModelMapper modelMapper;

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO dto) {
        log.info("Creating new order for userId={}, addressId={}, paymentMethodId={}",
                dto.getUserid(), dto.getAddressId(), dto.getPaymentMethodId());

        User user = userRepository.findById(dto.getUserid())
                .orElseThrow(() -> {
                    log.error("User not found with id={}", dto.getUserid());
                    return new ResourceNotFoundException("User not found");
                });

        Address address = addressRepository.findById(dto.getAddressId())
                .orElseThrow(() -> {
                    log.error("Address not found with id={}", dto.getAddressId());
                    return new ResourceNotFoundException("Address not found");
                });

        PaymentMethod paymentMethod = paymentMethodRepository.findById(dto.getPaymentMethodId())
                .orElseThrow(() -> {
                    log.error("Payment method not found with id={}", dto.getPaymentMethodId());
                    return new ResourceNotFoundException("Payment method not found");
                });

        Order order = Order.builder()
                .user(user)
                .address(address)
                .paymentMethod(paymentMethod)
                .totalAmount(dto.getTotalAmount())
                .status(dto.getStatus())
                .placed_at(LocalDateTime.now())
                .orderItems(new HashSet<>())
                .build();

        if (dto.getItems() != null) {
            dto.getItems().forEach(itemDTO -> {
                Product product = productRepository.findById(itemDTO.getProductId())
                        .orElseThrow(() -> {
                            log.error("Product not found with id={}", itemDTO.getProductId());
                            return new ResourceNotFoundException("Product not found");
                        });

                order.getOrderItems().add(OrderItem.builder()
                        .order(order)
                        .product(product)
                        .quantity(itemDTO.getQuantity())
                        .price(itemDTO.getPrice())
                        .build());
            });
        }

        Order savedOrder = orderRepository.save(order);
        log.debug("Order saved: {}", savedOrder);
        return modelMapper.map(savedOrder, OrderResponseDTO.class);
    }

    @Override
    public OrderResponseDTO getOrderById(Long orderId) {
        log.info("Fetching order with id={}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found with id={}", orderId);
                    return new ResourceNotFoundException("Order not found");
                });
        return modelMapper.map(order, OrderResponseDTO.class);
    }

    @Override
    public List<OrderResponseDTO> getOrdersByUserId(Long userId) {
        log.info("Fetching orders for userId={}", userId);
        List<Order> orders = orderRepository.findByUserId(userId);
        if (orders.isEmpty()) {
            log.error("No orders found for userId={}", userId);
            throw new ResourceNotFoundException("No orders found for user with id: " + userId);
        }
        log.debug("Found {} orders for userId={}", orders.size(), userId);
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        log.info("Fetching all orders");
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            log.error("No orders found in the system");
            throw new ResourceNotFoundException("No orders found");
        }
        log.debug("Found {} orders in total", orders.size());
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO updateOrderStatus(Long orderId, String status) {
        log.info("Updating status of orderId={} to '{}'", orderId, status);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found with id={}", orderId);
                    return new ResourceNotFoundException("Order not found");
                });
        order.setStatus(status);
        Order updated = orderRepository.save(order);
        log.debug("Order updated: {}", updated);
        return modelMapper.map(updated, OrderResponseDTO.class);
    }

    @Override
    public void deleteOrder(Long orderId) {
        log.info("Deleting order with id={}", orderId);
        if (!orderRepository.existsById(orderId)) {
            log.error("Order not found with id={}", orderId);
            throw new ResourceNotFoundException("Order not found");
        }
        orderRepository.deleteById(orderId);
        log.info("Order with id={} deleted successfully", orderId);
    }
}
