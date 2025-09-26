package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.orderItem.OrderItemRequestDTO;
import com.cognizant.ecommerce.dto.orderItem.OrderItemResponseDTO;
import com.cognizant.ecommerce.service.OrderItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; //
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<OrderItemResponseDTO> addOrderItem(@Valid @RequestBody OrderItemRequestDTO dto) {
        log.info("Received request to add order item: {}", dto);
        OrderItemResponseDTO response = orderItemService.addOrderItem(dto);
        log.info("Order item added successfully with id={} for orderId={}", response.getId(), dto.getId());
        return ResponseEntity.ok(response);
    }

    // Only owner or admin can view order items
    @PreAuthorize("@authService.canAccessOrder(#orderId)")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderItemResponseDTO>> getItemsByOrder(@PathVariable Long orderId) {
        log.info("Fetching order items for orderId={}", orderId);
        List<OrderItemResponseDTO> items = orderItemService.getOrderItemsByOrderId(orderId);
        log.debug("Fetched {} items for orderId={}", items.size(), orderId);
        return ResponseEntity.ok(items);
    }

    // Only admins can update order items
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<OrderItemResponseDTO> updateOrderItem(@Valid @PathVariable Long id, @Valid @RequestBody OrderItemRequestDTO dto) {
        log.info("Updating order item with id={} using data: {}", id, dto);
        OrderItemResponseDTO updatedItem = orderItemService.updateOrderItem(id, dto);
        log.info("Order item updated successfully with id={}", id);
        return ResponseEntity.ok(updatedItem);
    }

    // Only admins can delete order items
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<String> deleteOrderItem(@Valid @PathVariable Long id) {
        log.warn("Deleting order item with id={}", id);
        orderItemService.deleteOrderItem(id);
        log.info("Order item deleted successfully with id={}", id);
        return ResponseEntity.ok("Order Deleted");
    }
}