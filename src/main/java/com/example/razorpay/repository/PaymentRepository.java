package com.example.razorpay.repository;

import com.example.razorpay.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Payment entity persistence operations.
 * Provides database CRUD operations and custom query methods.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find payment by Razorpay Payment ID
     */
    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);

    /**
     * Find payment by Razorpay Order ID
     */
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    /**
     * Find all payments by customer email
     */
    List<Payment> findByEmail(String email);

    /**
     * Find all payments by status
     */
    List<Payment> findByStatus(Payment.PaymentStatus status);

    /**
     * Find all payments within a date range
     */
    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate ORDER BY p.createdAt DESC")
    List<Payment> findPaymentsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find verified payments
     */
    @Query("SELECT p FROM Payment p WHERE p.signatureVerified = true ORDER BY p.createdAt DESC")
    List<Payment> findVerifiedPayments();

    /**
     * Count successful payments
     */
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'CAPTURED'")
    Long countSuccessfulPayments();

    /**
     * Get total revenue from successful payments
     */
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'CAPTURED'")
    Long getTotalRevenue();

    /**
     * Get successful payments by email
     */
    @Query("SELECT p FROM Payment p WHERE p.email = :email AND p.status = 'CAPTURED' ORDER BY p.createdAt DESC")
    List<Payment> findSuccessfulPaymentsByEmail(@Param("email") String email);
}
