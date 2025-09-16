package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.OrderItemRepository;
import com.cognizant.ecommerce.dao.OrderRepository;
import com.cognizant.ecommerce.dao.ProductRepository;
import com.cognizant.ecommerce.dto.orderItem.OrderItemRequestDTO;
import com.cognizant.ecommerce.dto.orderItem.OrderItemResponseDTO;
import com.cognizant.ecommerce.model.Order;
import com.cognizant.ecommerce.model.OrderItem;
import com.cognizant.ecommerce.model.Product;
import com.cognizant.ecommerce.service.OrderItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @Override
    public OrderItemResponseDTO addOrderItem(OrderItemRequestDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Order order = orderRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderItem item = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(dto.getQuantity())
                .price(dto.getPrice())
                .build();

        return modelMapper.map(orderItemRepository.save(item), OrderItemResponseDTO.class);
    }

    @Override
    public List<OrderItemResponseDTO> getOrderItemsByOrderId(Long orderId) {
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return items.stream()
                .map(item -> modelMapper.map(item, OrderItemResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public OrderItemResponseDTO updateOrderItem(Long itemId, OrderItemRequestDTO dto) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Order item not found"));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        item.setProduct(product);
        item.setQuantity(dto.getQuantity());
        item.setPrice(dto.getPrice());

        return modelMapper.map(orderItemRepository.save(item), OrderItemResponseDTO.class);
    }

    @Override
    public void deleteOrderItem(Long itemId) {
        if (!orderItemRepository.existsById(itemId)) {
            throw new RuntimeException("Order item not found");
        }
        orderItemRepository.deleteById(itemId);
    }
}
