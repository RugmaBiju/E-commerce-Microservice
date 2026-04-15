package com.example.razorpay.service;

import com.example.razorpay.model.Payment;
import com.example.razorpay.repository.PaymentRepository;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for handling Razorpay webhook events.
 * Verifies webhook signatures and processes payment status updates.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private final PaymentRepository paymentRepository;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    /**
     * Verify the webhook signature from Razorpay
     *
     * @param payload   Raw webhook payload
     * @param signature X-Razorpay-Signature header value
     * @return          true if signature is valid
     */
    public boolean verifyWebhookSignature(String payload, String signature) {
        try {
            // Razorpay signature verification
            // Signature = HMAC_SHA256(payload, keySecret)
            return Utils.verifyWebhookSignature(payload, signature, keySecret);
        } catch (Exception e) {
            log.error("Webhook signature verification failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Process the webhook event and update payment status
     *
     * @param payload JSON payload from Razorpay
     */
    public void processWebhookEvent(String payload) {
        try {
            JSONObject event = new JSONObject(payload);
            String eventType = event.getString("event");
            JSONObject eventData = event.getJSONObject("payload").getJSONObject("payment").getJSONObject("entity");

            log.info("Processing webhook event: {}", eventType);

            switch (eventType) {
                case "payment.authorized":
                    handlePaymentAuthorized(eventData);
                    break;

                case "payment.captured":
                    handlePaymentCaptured(eventData);
                    break;

                case "payment.failed":
                    handlePaymentFailed(eventData);
                    break;

                case "order.paid":
                    handleOrderPaid(eventData);
                    break;

                case "refund.created":
                    handleRefundCreated(eventData);
                    break;

                default:
                    log.debug("Unknown event type: {}", eventType);
            }

        } catch (Exception e) {
            log.error("Error parsing webhook payload: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process webhook event", e);
        }
    }

    /**
     * Handle payment.authorized event
     */
    private void handlePaymentAuthorized(JSONObject paymentData) {
        String paymentId = paymentData.getString("id");
        log.info("Payment authorized: {}", paymentId);

        paymentRepository.findByRazorpayPaymentId(paymentId)
                .ifPresent(payment -> {
                    payment.setStatus(Payment.PaymentStatus.AUTHORIZED);
                    payment.setPaymentMethod(paymentData.optString("method", "unknown"));
                    paymentRepository.save(payment);
                    log.info("Updated payment status to AUTHORIZED: {}", paymentId);
                });
    }

    /**
     * Handle payment.captured event (successful payment)
     */
    private void handlePaymentCaptured(JSONObject paymentData) {
        String paymentId = paymentData.getString("id");
        log.info("Payment captured (successful): {}", paymentId);

        paymentRepository.findByRazorpayPaymentId(paymentId)
                .ifPresent(payment -> {
                    payment.setStatus(Payment.PaymentStatus.CAPTURED);
                    payment.setPaymentMethod(paymentData.optString("method", "unknown"));
                    payment.setRazorpayResponse(paymentData.toString());
                    paymentRepository.save(payment);
                    log.info("Updated payment status to CAPTURED: {}", paymentId);
                });
    }

    /**
     * Handle payment.failed event
     */
    private void handlePaymentFailed(JSONObject paymentData) {
        String paymentId = paymentData.getString("id");
        String failureReason = paymentData.optString("description", "Unknown error");
        log.warn("Payment failed: {} - {}", paymentId, failureReason);

        paymentRepository.findByRazorpayPaymentId(paymentId)
                .ifPresent(payment -> {
                    payment.setStatus(Payment.PaymentStatus.FAILED);
                    payment.setErrorMessage(failureReason);
                    paymentRepository.save(payment);
                    log.info("Updated payment status to FAILED: {}", paymentId);
                });
    }

    /**
     * Handle order.paid event (also indicates successful payment)
     */
    private void handleOrderPaid(JSONObject paymentData) {
        String orderId = paymentData.optString("order_id", "");
        String paymentId = paymentData.optString("id", "");
        log.info("Order paid successfully: {} - Payment: {}", orderId, paymentId);

        if (!orderId.isEmpty()) {
            paymentRepository.findByRazorpayOrderId(orderId)
                    .ifPresent(payment -> {
                        if (payment.getStatus() != Payment.PaymentStatus.CAPTURED) {
                            payment.setStatus(Payment.PaymentStatus.CAPTURED);
                            payment.setRazorpayPaymentId(paymentId);
                            paymentRepository.save(payment);
                            log.info("Updated order payment status to CAPTURED: {}", orderId);
                        }
                    });
        }
    }

    /**
     * Handle refund.created event
     */
    private void handleRefundCreated(JSONObject refundData) {
        String paymentId = refundData.optString("payment_id", "");
        String refundId = refundData.getString("id");
        String refundAmount = refundData.optString("amount", "0");
        log.info("Refund initiated: {} for payment: {} (amount: {})", refundId, paymentId, refundAmount);

        if (!paymentId.isEmpty()) {
            paymentRepository.findByRazorpayPaymentId(paymentId)
                    .ifPresent(payment -> {
                        payment.setStatus(Payment.PaymentStatus.REFUNDED);
                        paymentRepository.save(payment);
                        log.info("Updated payment status to REFUNDED: {}", paymentId);
                    });
        }
    }
}
