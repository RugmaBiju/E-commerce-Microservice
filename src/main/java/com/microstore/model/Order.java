package com.microstore.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Order Entity - Represents a customer order.
 * Demonstrates: Inheritance, Encapsulation, Collections (ArrayList),
 * Method Overriding, Method Overloading
 */
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "total_amount")
    private double totalAmount;

    @Column(name = "status")
    private String status = "PENDING"; // PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED

    @Column(name = "shipping_address_id")
    private Long shippingAddressId;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "payment_status")
    private String paymentStatus = "PENDING"; // PENDING, PAID, FAILED, REFUNDED

    @Column(name = "shipping_name")
    private String shippingName;

    @Column(name = "shipping_street")
    private String shippingStreet;

    @Column(name = "shipping_city")
    private String shippingCity;

    @Column(name = "shipping_state")
    private String shippingState;

    @Column(name = "shipping_zip")
    private String shippingZip;

    @Column(name = "shipping_phone")
    private String shippingPhone;

    /** Collection usage - ArrayList of OrderItems */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    // Constructors
    public Order() {
        super();
    }

    public Order(Long userId) {
        super();
        this.userId = userId;
    }

    public Order(Long userId, double totalAmount, String status) {
        super();
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    @Override
    public String getEntityType() {
        return "ORDER";
    }

    @Override
    public String getDisplayInfo() {
        return "Order #" + getId() + " - ₹" + totalAmount + " (" + status + ")";
    }

    /** Method overloading - display with item count */
    public String getDisplayInfo(int itemCount) {
        return "Order #" + getId() + " - " + itemCount + " items - ₹" + totalAmount;
    }

    /**
     * Adds an item to the order. Demonstrates use of collections.
     */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    /**
     * Calculates the total from items.
     */
    public double calculateTotal() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getShippingAddressId() { return shippingAddressId; }
    public void setShippingAddressId(Long shippingAddressId) { this.shippingAddressId = shippingAddressId; }
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getShippingName() { return shippingName; }
    public void setShippingName(String shippingName) { this.shippingName = shippingName; }
    public String getShippingStreet() { return shippingStreet; }
    public void setShippingStreet(String shippingStreet) { this.shippingStreet = shippingStreet; }
    public String getShippingCity() { return shippingCity; }
    public void setShippingCity(String shippingCity) { this.shippingCity = shippingCity; }
    public String getShippingState() { return shippingState; }
    public void setShippingState(String shippingState) { this.shippingState = shippingState; }
    public String getShippingZip() { return shippingZip; }
    public void setShippingZip(String shippingZip) { this.shippingZip = shippingZip; }
    public String getShippingPhone() { return shippingPhone; }
    public void setShippingPhone(String shippingPhone) { this.shippingPhone = shippingPhone; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    @Override
    public String toString() {
        return "Order{id=" + getId() + ", userId=" + userId + ", total=" + totalAmount + ", status='" + status + "'}";
    }
}
