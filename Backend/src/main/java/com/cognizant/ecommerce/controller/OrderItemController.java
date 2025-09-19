package com.cognizant.ecommerce.controller;

import com.cognizant.ecommerce.dto.orderItem.OrderItemRequestDTO;
import com.cognizant.ecommerce.dto.orderItem.OrderItemResponseDTO;
import com.cognizant.ecommerce.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    //add order item
    @PostMapping
    public ResponseEntity<OrderItemResponseDTO> addOrderItem(@RequestBody OrderItemRequestDTO dto) {
        return ResponseEntity.ok(orderItemService.addOrderItem(dto));
    }

    //Get items by order id
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderItemResponseDTO>> getItemsByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderItemService.getOrderItemsByOrderId(orderId));
    }

    //update order item
    @PutMapping("/{id}")
    public ResponseEntity<OrderItemResponseDTO> updateOrderItem(@PathVariable Long id, @RequestBody OrderItemRequestDTO dto) {
        return ResponseEntity.ok(orderItemService.updateOrderItem(id, dto));
    }

    //delete order item
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrderItem(@PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.ok("Order Deleted");
    }
}
