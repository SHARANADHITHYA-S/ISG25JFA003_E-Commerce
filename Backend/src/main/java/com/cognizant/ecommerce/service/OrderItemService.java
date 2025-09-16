package com.cognizant.ecommerce.service;

import com.cognizant.ecommerce.dto.orderItem.OrderItemRequestDTO;
import com.cognizant.ecommerce.dto.orderItem.OrderItemResponseDTO;

import java.util.List;

public interface OrderItemService {

    OrderItemResponseDTO addOrderItem(OrderItemRequestDTO orderItemRequestDTO);

    List<OrderItemResponseDTO> getOrderItemsByOrderId(Long orderId);

    OrderItemResponseDTO updateOrderItem(Long orderItemId, OrderItemRequestDTO orderItemRequestDTO);

    void deleteOrderItem(Long orderItemId);
}
