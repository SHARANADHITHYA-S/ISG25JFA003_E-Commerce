package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.OrderItemRepository;
import com.cognizant.ecommerce.dao.OrderRepository;
import com.cognizant.ecommerce.dao.ProductRepository;
import com.cognizant.ecommerce.dto.orderItem.OrderItemRequestDTO;
import com.cognizant.ecommerce.dto.orderItem.OrderItemResponseDTO;
import com.cognizant.ecommerce.exception.ResourceNotFoundException;
import com.cognizant.ecommerce.model.Order;
import com.cognizant.ecommerce.model.OrderItem;
import com.cognizant.ecommerce.model.Product;
import com.cognizant.ecommerce.service.OrderItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    private static final Logger log = LoggerFactory.getLogger(OrderItemServiceImpl.class);

    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @Override
    public OrderItemResponseDTO addOrderItem(OrderItemRequestDTO dto) {
        log.info("Adding new order item for productId={} to orderId={}", dto.getProductId(), dto.getId());

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> {
                    log.error("Product not found with id={}", dto.getProductId());
                    return new ResourceNotFoundException("Product not found");
                });

        Order order = orderRepository.findById(dto.getId())
                .orElseThrow(() -> {
                    log.error("Order not found with id={}", dto.getId());
                    return new ResourceNotFoundException("Order not found");
                });

        OrderItem item = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(dto.getQuantity())
                .price(dto.getPrice())
                .build();

        OrderItem savedItem = orderItemRepository.save(item);
        log.debug("Order item saved: {}", savedItem);

        return modelMapper.map(savedItem, OrderItemResponseDTO.class);
    }

    @Override
    public List<OrderItemResponseDTO> getOrderItemsByOrderId(Long orderId) {
        log.info("Fetching order items for orderId={}", orderId);

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        if (items.isEmpty()) {
            log.error("No order items found for orderId={}", orderId);
            throw new ResourceNotFoundException("No order items found for order with id: " + orderId);
        }

        log.debug("Found {} order items for orderId={}", items.size(), orderId);
        return items.stream()
                .map(item -> modelMapper.map(item, OrderItemResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public OrderItemResponseDTO updateOrderItem(Long itemId, OrderItemRequestDTO dto) {
        log.info("Updating order item with id={}", itemId);

        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Order item not found with id={}", itemId);
                    return new ResourceNotFoundException("Order item not found");
                });

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> {
                    log.error("Product not found with id={}", dto.getProductId());
                    return new ResourceNotFoundException("Product not found");
                });

        item.setProduct(product);
        item.setQuantity(dto.getQuantity());
        item.setPrice(dto.getPrice());

        OrderItem updatedItem = orderItemRepository.save(item);
        log.debug("Order item updated: {}", updatedItem);

        return modelMapper.map(updatedItem, OrderItemResponseDTO.class);
    }

    @Override
    public void deleteOrderItem(Long itemId) {
        log.info("Deleting order item with id={}", itemId);

        if (!orderItemRepository.existsById(itemId)) {
            log.error("Order item not found with id={}", itemId);
            throw new ResourceNotFoundException("Order item not found");
        }

        orderItemRepository.deleteById(itemId);
        log.info("Order item with id={} deleted successfully", itemId);
    }
}
