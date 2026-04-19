package com.microstore.service;

import com.microstore.exception.ResourceNotFoundException;
import com.microstore.model.*;
import com.microstore.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * OrderService - Business logic for Order management.
 * Demonstrates: Collections, Exception Handling, Transactional operations
 */
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Create an order from cart items.
     */
    @Transactional
    public Order createOrderFromCart(Long userId, Map<String, String> shippingInfo) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        Order order = new Order(userId);
        order.setShippingName(shippingInfo.getOrDefault("name", ""));
        order.setShippingStreet(shippingInfo.getOrDefault("street", ""));
        order.setShippingCity(shippingInfo.getOrDefault("city", ""));
        order.setShippingState(shippingInfo.getOrDefault("state", ""));
        order.setShippingZip(shippingInfo.getOrDefault("zip", ""));
        order.setShippingPhone(shippingInfo.getOrDefault("phone", ""));
        order.setStatus("PENDING");
        order.setPaymentStatus("PENDING");

        double total = 0;

        // Using ArrayList to build order items
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductBrand(product.getBrand());
            orderItem.setProductImage(product.getImageUrl());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getEffectivePrice());
            orderItem.setSelectedSize(cartItem.getSelectedSize());

            total += orderItem.getSubtotal();
            orderItems.add(orderItem);

            // Reduce stock
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }

        // Add delivery fee
        double deliveryFee = total > 999 ? 0 : 99;
        order.setTotalAmount(total + deliveryFee);
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        // Clear cart
        cartItemRepository.deleteByUserId(userId);

        return savedOrder;
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Order> findAll() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    public Order updateStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public Order updatePaymentStatus(Long orderId, String paymentStatus, String paymentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        order.setPaymentStatus(paymentStatus);
        order.setPaymentId(paymentId);
        if ("PAID".equals(paymentStatus)) {
            order.setStatus("CONFIRMED");
        }
        return orderRepository.save(order);
    }

    /**
     * Get order statistics - demonstrates HashMap usage.
     */
    public Map<String, Object> getOrderStats() {
        Map<String, Object> stats = new HashMap<>();
        List<Order> allOrders = orderRepository.findAll();
        stats.put("totalOrders", allOrders.size());
        stats.put("pendingOrders", orderRepository.findByStatus("PENDING").size());
        stats.put("confirmedOrders", orderRepository.findByStatus("CONFIRMED").size());
        stats.put("deliveredOrders", orderRepository.findByStatus("DELIVERED").size());
        stats.put("totalRevenue", allOrders.stream()
                .filter(o -> "PAID".equals(o.getPaymentStatus()))
                .mapToDouble(Order::getTotalAmount).sum());
        return stats;
    }
}
