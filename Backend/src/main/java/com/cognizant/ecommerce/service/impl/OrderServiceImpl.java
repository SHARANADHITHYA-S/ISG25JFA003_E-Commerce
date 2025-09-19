package com.cognizant.ecommerce.service.impl;

import com.cognizant.ecommerce.dao.*;
import com.cognizant.ecommerce.dto.order.OrderRequestDTO;
import com.cognizant.ecommerce.dto.order.OrderResponseDTO;
import com.cognizant.ecommerce.exception.ResourceNotFoundException;
import com.cognizant.ecommerce.model.*;
import com.cognizant.ecommerce.service.CartService;
import com.cognizant.ecommerce.service.OrderService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private CartService cartService;

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ModelMapper modelMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public OrderResponseDTO createOrder(Long userId, Long addressId, Long paymentMethodId) {
        log.info("Creating new order for userId={}, addressId={}, paymentMethodId={}",
                userId, addressId, paymentMethodId);

        // 1. Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // 2. Fetch address
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        // 3. Fetch payment method
        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with id: " + paymentMethodId));

        // 4. Fetch cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user id: " + userId));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty for user id: " + userId);
        }

        // 5. Create order
        Order order = Order.builder()
                .user(user)
                .address(address)
                .paymentMethod(paymentMethod)
                .status("PENDING")
                .placed_at(LocalDateTime.now())
                .orderItems(new HashSet<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 6. Loop through cart items
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();

            // Stock check
            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            // Reduce stock
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            // Create order item
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .price(product.getPrice())
                    .build();

            order.getOrderItems().add(orderItem);

            // Add to total
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        order.setTotalAmount(totalAmount);

        // 7. Save order
        Order savedOrder = orderRepository.save(order);

        //flush persistence context
        entityManager.flush();
        entityManager.clear();


        //delete cart
        cartRepository.deleteByUserId(userId);


        log.info("Order created successfully for userId={} with orderId={}", userId, savedOrder.getId());

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
    public OrderResponseDTO deleteOrder(Long orderId) {
        log.info("Deleting order with id={}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found with id={}", orderId);
                    return new ResourceNotFoundException("Order not found");
                });
        order.setStatus("CANCELLED");
        Order deleted = orderRepository.save(order);
        log.debug("Order deleted: {}", deleted);
        return modelMapper.map(deleted, OrderResponseDTO.class);
    }
}
