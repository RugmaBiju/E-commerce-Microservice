package com.example.razorpay.controller;

import com.example.razorpay.dto.ApiResponse;
import com.example.razorpay.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Webhook controller to handle asynchronous events from Razorpay.
 * Razorpay will POST events to this endpoint (configured in Razorpay dashboard).
 *
 * Supports events:
 *  - payment.authorized: Payment has been authorized
 *  - payment.captured: Payment has been captured (successful)
 *  - payment.failed: Payment has failed
 *  - order.paid: Order has been paid (also signals payment success)
 *  - refund.created: Refund has been initiated
 */
@Slf4j
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookService webhookService;

    /**
     * Webhook endpoint for Razorpay events.
     * 
     * @param payload    JSON payload from Razorpay
     * @param signature  X-Razorpay-Signature header
     * @return           Response status
     */
    @PostMapping("/razorpay")
    public ResponseEntity<ApiResponse<?>> handleRazorpayWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Razorpay-Signature") String signature) {

        log.info("Received Razorpay webhook");

        try {
            // Verify webhook signature
            if (!webhookService.verifyWebhookSignature(payload, signature)) {
                log.warn("Webhook signature verification failed");
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Signature verification failed", "WEBHOOK_SIGNATURE_INVALID"));
            }

            // Process the webhook event
            webhookService.processWebhookEvent(payload);

            log.info("Webhook processed successfully");
            return ResponseEntity.ok(ApiResponse.success(
                    Map.of("status", "acknowledged"),
                    "Webhook received and processed"
            ));

        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error processing webhook: " + e.getMessage(), "WEBHOOK_PROCESSING_ERROR"));
        }
    }
}
