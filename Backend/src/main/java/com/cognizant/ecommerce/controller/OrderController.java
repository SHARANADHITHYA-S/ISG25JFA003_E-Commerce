package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.order.OrderRequestDTO;
import com.cognizant.ecommerce.dto.order.OrderResponseDTO;
import com.cognizant.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // ➕ Create a new order
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
        OrderResponseDTO response = orderService.createOrder(orderRequestDTO);
        return ResponseEntity.ok(response);
    }

    // 🔍 Get order by ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // 📦 Get all orders
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // 👤 Get orders by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByUserId(@PathVariable Long userId) {
        return  ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    //http://localhost:8080/api/orders/35/status?status=SHIPPED
    // Update order status
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // ❌ Delete order
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Order Deleted");
    }
}
