package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dao.CartItemRepository;
import com.cognizant.ecommerce.dao.CartRepository;
import com.cognizant.ecommerce.dto.order.OrderRequestDTO;
import com.cognizant.ecommerce.dto.order.OrderResponseDTO;
import com.cognizant.ecommerce.service.CartService;
import com.cognizant.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // ✅ Lombok logging
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CartRepository cartRepository;
    private final OrderService orderService;
    private final CartService cartService;
    private final CartItemRepository cartItemRepository;

    // Create a new order
    @PreAuthorize("@authService.isSelfOrAdmin(#orderRequestDTO.userid)")
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        log.info("Received request to create order for userId={}, addressId={}, paymentMethodId={}",
                orderRequestDTO.getUserid(), orderRequestDTO.getAddressId(), orderRequestDTO.getPaymentMethodId());

        OrderResponseDTO createdOrder = orderService.createOrder(
                orderRequestDTO.getUserid(),
                orderRequestDTO.getAddressId(),
                orderRequestDTO.getPaymentMethodId()
        );

        log.info("Order created successfully with orderId={}", createdOrder.getId());
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    // Get order by order ID
    @PreAuthorize("@authService.canAccessOrder(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        log.info("Fetching order with id={}", id);
        OrderResponseDTO order = orderService.getOrderById(id);
        log.debug("Fetched order details: {}", order);
        return ResponseEntity.ok(order);
    }

    // Get all orders
    @GetMapping("/admin")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        log.info("Fetching all orders");
        List<OrderResponseDTO> orders = orderService.getAllOrders();
        log.debug("Total orders fetched: {}", orders.size());
        return ResponseEntity.ok(orders);
    }

    // Get orders by user ID
    @PreAuthorize("@authService.isSelfOrAdmin(#userId)")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByUserId(@PathVariable Long userId) {
        log.info("Fetching orders for userId={}", userId);
        List<OrderResponseDTO> orders = orderService.getOrdersByUserId(userId);
        log.debug("Total orders fetched for userId {}: {}", userId, orders.size());
        return ResponseEntity.ok(orders);
    }

    // Update order status
    @PutMapping("/admin/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(@Valid @PathVariable Long id,@Valid @RequestParam String status) {
        log.info("Updating status of orderId={} to {}", id, status);
        orderService.updateOrderStatus(id, status);
        OrderResponseDTO updatedOrder = orderService.getOrderById(id);
        log.info("Order status updated successfully for orderId={}", id);
        return ResponseEntity.ok(updatedOrder);
    }

    // Delete order
    @PutMapping("/admin/{id}")
    public ResponseEntity<String> deleteOrder(@Valid @PathVariable Long id) {
        log.warn("Deleting order with id={}", id);
        orderService.deleteOrder(id);
        log.info("Order deleted successfully with id={}", id);
        return ResponseEntity.ok("Order Deleted");
    }
}
