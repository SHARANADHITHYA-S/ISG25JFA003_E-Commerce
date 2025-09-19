package com.cognizant.ecommerce.service;

import com.cognizant.ecommerce.dto.order.OrderRequestDTO;
import com.cognizant.ecommerce.dto.order.OrderResponseDTO;

import java.util.List;

public interface OrderService {

    OrderResponseDTO createOrder(Long UserId, Long AddressId, Long PaymentMethodId);

    OrderResponseDTO getOrderById(Long orderId);

    List<OrderResponseDTO> getOrdersByUserId(Long userId);

    List<OrderResponseDTO> getAllOrders();

    OrderResponseDTO updateOrderStatus(Long orderId, String status);

    OrderResponseDTO deleteOrder(Long orderId);
}
