package com.example.razorpay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Entity for storing payment records in the database.
 * Tracks all Razorpay transaction details for audit and reporting purposes.
 */
@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_razorpay_payment_id", columnList = "razorpay_payment_id"),
        @Index(name = "idx_razorpay_order_id", columnList = "razorpay_order_id"),
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Razorpay Payment ID (unique) - e.g., pay_XXXXXXXXXX */
    @Column(name = "razorpay_payment_id", nullable = false, unique = true)
    private String razorpayPaymentId;

    /** Razorpay Order ID (unique) - e.g., order_XXXXXXXXX */
    @Column(name = "razorpay_order_id", nullable = false, unique = true)
    private String razorpayOrderId;

    /** Customer's full name */
    @Column(name = "name", nullable = false)
    private String name;

    /** Customer's email address */
    @Column(name = "email", nullable = false)
    private String email;

    /** Customer's phone number */
    @Column(name = "phone", nullable = false)
    private String phone;

    /** Amount in smallest currency unit (paise for INR) */
    @Column(name = "amount", nullable = false)
    private Long amount;

    /** Currency code (e.g., INR) */
    @Column(name = "currency", nullable = false)
    private String currency;

    /** Product description */
    @Column(name = "description")
    private String description;

    /** Payment method used (card, upi, netbanking, etc.) */
    @Column(name = "payment_method")
    private String paymentMethod;

    /** Payment status: CREATED, AUTHORIZED, CAPTURED, FAILED, REFUNDED */
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    /** HMAC-SHA256 signature for verification */
    @Column(name = "razorpay_signature")
    private String razorpaySignature;

    /** Whether the signature has been verified */
    @Column(name = "signature_verified", nullable = false)
    private Boolean signatureVerified = false;

    /** Response from Razorpay API (stored as JSON) */
    @Column(name = "razorpay_response", columnDefinition = "TEXT")
    private String razorpayResponse;

    /** Error message if payment failed */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /** Timestamp when payment was created in database */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp when payment was last updated */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Enum for Payment Status
     */
    public enum PaymentStatus {
        CREATED,      // Order/Payment created but not yet processed
        AUTHORIZED,   // Payment authorized
        CAPTURED,     // Payment captured/successful
        FAILED,       // Payment failed
        REFUNDED,     // Payment refunded
        CANCELLED,    // Payment cancelled by user
        PENDING       // Payment pending confirmation
    }
}
