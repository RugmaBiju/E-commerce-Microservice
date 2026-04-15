package com.example.razorpay.service;

import com.example.razorpay.dto.OrderRequest;
import com.example.razorpay.dto.OrderResponse;
import com.example.razorpay.dto.PaymentVerificationRequest;
import com.example.razorpay.model.Payment;
import com.example.razorpay.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Core service that interacts with the Razorpay SDK.
 * Responsibilities:
 *  1. createOrder       – Calls Razorpay API to create an order and returns an OrderResponse.
 *  2. verifyPayment     – Validates the HMAC-SHA256 signature after a successful checkout.
 *  3. persistPayment    – Saves payment record to database.
 *  4. fetchPaymentBind  – Retrieves payment details from DB and Razorpay.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    // ──────────────────────────────────────────────
    // 1. Create Razorpay Order
    // ──────────────────────────────────────────────

    /**
     * Creates a Razorpay order via the Orders API.
     *
     * @param request  DTO from the frontend (amount in paise, currency, customer details)
     * @return         OrderResponse with orderId + key consumed by Razorpay Checkout JS
     * @throws RazorpayException on API/network failure
     */
    @Transactional
    public OrderResponse createOrder(OrderRequest request) throws RazorpayException {
        log.info("Creating Razorpay order: amount={} currency={} email={}",
                request.getAmount(), request.getCurrency(), request.getEmail());

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", request.getAmount());          // in paise
        orderRequest.put("currency",
                request.getCurrency() != null ? request.getCurrency() : "INR");
        orderRequest.put("receipt", "rcpt_" + System.currentTimeMillis());
        orderRequest.put("payment_capture", 1);                   // auto-capture

        // Optional: attach custom notes
        JSONObject notes = new JSONObject();
        notes.put("name", request.getName());
        notes.put("email", request.getEmail());
        notes.put("phone", request.getPhone());
        orderRequest.put("notes", notes);

        Order order = razorpayClient.orders.create(orderRequest);
        String orderId = order.get("id").toString();
        log.info("Razorpay order created: id={}", orderId);

        // Persist payment record with initial status
        Payment payment = Payment.builder()
                .razorpayOrderId(orderId)
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .amount(request.getAmount())
                .currency(request.getCurrency() != null ? request.getCurrency() : "INR")
                .description(request.getDescription())
                .status(Payment.PaymentStatus.CREATED)
                .signatureVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);
        log.info("Payment record created in database for order: {}", orderId);

        return OrderResponse.builder()
                .orderId(orderId)
                .razorpayKeyId(keyId)
                .amount(request.getAmount())
                .currency(request.getCurrency() != null ? request.getCurrency() : "INR")
                .description(request.getDescription())
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .callbackUrl("/payment/success")
                .build();
    }

    // ──────────────────────────────────────────────
    // 2. Verify Payment Signature
    // ──────────────────────────────────────────────

    /**
     * Verifies the HMAC-SHA256 payment signature returned by Razorpay after checkout.
     * The expected signature = HMAC_SHA256(orderId + "|" + paymentId, keySecret).
     * Also persists the payment to database with verified status.
     *
     * @param request  Contains paymentId, orderId, and signature from the frontend callback
     * @return         true if signature is valid, false if tampered
     */
    @Transactional
    public boolean verifyPaymentSignature(PaymentVerificationRequest request) {
        try {
            Map<String, String> attributes = new HashMap<>();
            attributes.put("razorpay_payment_id", request.getRazorpayPaymentId());
            attributes.put("razorpay_order_id",   request.getRazorpayOrderId());
            attributes.put("razorpay_signature",  request.getRazorpaySignature());

            Utils.verifyPaymentSignature(new JSONObject(attributes), keySecret);
            log.info("Payment signature verified OK: paymentId={}", request.getRazorpayPaymentId());

            // Update payment record with verified details
            Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                    .orElseThrow(() -> new RuntimeException("Payment not found for order: " + request.getRazorpayOrderId()));

            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
            payment.setRazorpaySignature(request.getRazorpaySignature());
            payment.setSignatureVerified(true);
            payment.setStatus(Payment.PaymentStatus.CAPTURED);
            payment.setUpdatedAt(LocalDateTime.now());

            // Fetch payment details from Razorpay API
            try {
                com.razorpay.Payment razorpayPayment = razorpayClient.payments.fetch(request.getRazorpayPaymentId());
                payment.setPaymentMethod(razorpayPayment.get("method").toString());
                payment.setRazorpayResponse(razorpayPayment.toString());
                
                // Check if it's actually captured
                if ("captured".equals(razorpayPayment.get("status").toString())) {
                    payment.setStatus(Payment.PaymentStatus.CAPTURED);
                } else if ("authorized".equals(razorpayPayment.get("status").toString())) {
                    payment.setStatus(Payment.PaymentStatus.AUTHORIZED);
                }
            } catch (RazorpayException e) {
                log.warn("Could not fetch payment details from Razorpay: {}", e.getMessage());
            }

            paymentRepository.save(payment);
            log.info("Payment verified and persisted to database: {}", request.getRazorpayPaymentId());
            return true;

        } catch (RazorpayException e) {
            log.error("Payment signature verification FAILED: {}", e.getMessage());
            
            // Update payment record with failure
            try {
                Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                        .orElse(null);
                if (payment != null) {
                    payment.setStatus(Payment.PaymentStatus.FAILED);
                    payment.setErrorMessage("Signature verification failed: " + e.getMessage());
                    payment.setUpdatedAt(LocalDateTime.now());
                    paymentRepository.save(payment);
                }
            } catch (Exception ex) {
                log.error("Error updating payment status: {}", ex.getMessage());
            }
            return false;
        }
    }

    // ──────────────────────────────────────────────
    // 3. Fetch Payment Details (optional helper)
    // ──────────────────────────────────────────────

    /**
     * Fetches full payment details from Razorpay by paymentId.
     * Useful for displaying a detailed receipt or storing in your DB.
     *
     * @param paymentId  Razorpay payment ID (prefix: pay_)
     * @return           JSON string of payment details
     */
    public String fetchPaymentDetails(String paymentId) {
        try {
            com.razorpay.Payment payment = razorpayClient.payments.fetch(paymentId);
            log.debug("Fetched payment details: {}", payment.toString());
            return payment.toString();
        } catch (RazorpayException e) {
            log.error("Failed to fetch payment details for paymentId={}: {}", paymentId, e.getMessage());
            return "{}";
        }
    }

    // ──────────────────────────────────────────────
    // 4. Payment Management Methods
    // ──────────────────────────────────────────────

    /**
     * Get payment from database by payment ID
     */
    public Payment getPaymentByPaymentId(String paymentId) {
        return paymentRepository.findByRazorpayPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
    }

    /**
     * Get payment from database by order ID
     */
    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findByRazorpayOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
    }

    /**
     * Mark payment as failed
     */
    @Transactional
    public void markPaymentAsFailed(String orderId, String errorMessage) {
        Payment payment = paymentRepository.findByRazorpayOrderId(orderId)
                .orElse(null);
        
        if (payment != null) {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setErrorMessage(errorMessage);
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);
            log.warn("Marked payment as FAILED: {} - {}", orderId, errorMessage);
        }
    }

    /**
     * Get total successful transactions count
     */
    public Long getSuccessfulTransactionCount() {
        return paymentRepository.countSuccessfulPayments();
    }

    /**
     * Get total revenue from successful payments (in paise)
     */
    public Long getTotalRevenue() {
        Long total = paymentRepository.getTotalRevenue();
        return total != null ? total : 0L;
    }
}
