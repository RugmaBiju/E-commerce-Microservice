package com.microstore.controller;

import com.microstore.dto.ApiResponse;
import com.microstore.model.Order;
import com.microstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * OrderController - REST API for Order management.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /** Create order from cart */
    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<Order>> createOrder(
            @PathVariable Long userId,
            @RequestBody Map<String, String> shippingInfo) {
        Order order = orderService.createOrderFromCart(userId, shippingInfo);
        return ResponseEntity.ok(ApiResponse.success("Order created", order));
    }

    /** Get order by ID */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrder(@PathVariable Long id) {
        Order order = orderService.findById(id)
                .orElseThrow(() -> new com.microstore.exception.ResourceNotFoundException("Order", "id", id));
        return ResponseEntity.ok(ApiResponse.success("Order found", order));
    }

    /** Get orders by user */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Order>>> getUserOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("User orders", orderService.findByUserId(userId)));
    }

    /** Get all orders (admin) */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders() {
        return ResponseEntity.ok(ApiResponse.success("All orders", orderService.findAll()));
    }

    /** Update order status */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Order>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        Order updated = orderService.updateStatus(id, request.get("status"));
        return ResponseEntity.ok(ApiResponse.success("Order status updated", updated));
    }

    /** Get order statistics */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        return ResponseEntity.ok(ApiResponse.success("Order stats", orderService.getOrderStats()));
    }
}
