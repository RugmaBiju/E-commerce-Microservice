package com.microstore.model;

import jakarta.persistence.*;

/**
 * Payment Entity - Represents Razorpay payment records.
 * Demonstrates: Inheritance, Encapsulation, Method Overriding
 */
@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "razorpay_order_id")
    private String razorpayOrderId;

    @Column(name = "razorpay_payment_id")
    private String razorpayPaymentId;

    @Column(name = "razorpay_signature")
    private String razorpaySignature;

    @Column(name = "amount")
    private double amount;

    @Column(name = "currency")
    private String currency = "INR";

    @Column(name = "status")
    private String status = "CREATED"; // CREATED, AUTHORIZED, CAPTURED, FAILED, REFUNDED

    @Column(name = "method")
    private String method; // card, upi, netbanking, wallet

    // Constructors
    public Payment() {
        super();
    }

    public Payment(Long orderId, double amount) {
        super();
        this.orderId = orderId;
        this.amount = amount;
    }

    public Payment(Long orderId, String razorpayOrderId, double amount, String currency) {
        super();
        this.orderId = orderId;
        this.razorpayOrderId = razorpayOrderId;
        this.amount = amount;
        this.currency = currency;
    }

    @Override
    public String getEntityType() {
        return "PAYMENT";
    }

    @Override
    public String getDisplayInfo() {
        return "Payment ₹" + amount + " (" + status + ")";
    }

    public boolean isSuccessful() {
        return "CAPTURED".equalsIgnoreCase(status);
    }

    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }
    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }
    public String getRazorpaySignature() { return razorpaySignature; }
    public void setRazorpaySignature(String razorpaySignature) { this.razorpaySignature = razorpaySignature; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    @Override
    public String toString() {
        return "Payment{orderId=" + orderId + ", amount=" + amount + ", status='" + status + "'}";
    }
}
