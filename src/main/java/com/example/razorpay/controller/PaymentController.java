package com.example.razorpay.controller;

import com.example.razorpay.dto.OrderRequest;
import com.example.razorpay.dto.OrderResponse;
import com.example.razorpay.dto.PaymentVerificationRequest;
import com.example.razorpay.service.PaymentService;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * PaymentController exposes both:
 *  - MVC endpoints      → render Thymeleaf views (GET)
 *  - REST API endpoints → JSON for AJAX calls (POST)
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    // ──────────────────────────────────────────────
    // MVC (Page) Endpoints
    // ──────────────────────────────────────────────

    /** Home page — product listing / checkout form */
    @GetMapping("/checkout")
    public String checkoutPage(Model model) {
        model.addAttribute("orderRequest", new OrderRequest());
        return "checkout";
    }

    /** Payment success page */
    @GetMapping("/success")
    public String successPage(@RequestParam(required = false) String paymentId,
                              @RequestParam(required = false) String orderId,
                              Model model) {
        model.addAttribute("paymentId", paymentId);
        model.addAttribute("orderId", orderId);
        if (paymentId != null) {
            // Fetch extra details from Razorpay (optional)
            String details = paymentService.fetchPaymentDetails(paymentId);
            model.addAttribute("paymentDetails", details);
        }
        return "success";
    }

    /** Payment failure / cancellation page */
    @GetMapping("/failure")
    public String failurePage(@RequestParam(required = false) String reason, Model model) {
        model.addAttribute("reason", reason != null ? reason : "Payment was not completed.");
        return "failure";
    }

    // ──────────────────────────────────────────────
    // REST API Endpoints (called via AJAX / fetch)
    // ──────────────────────────────────────────────

    /**
     * POST /payment/create-order
     * Creates a Razorpay order and returns the orderId + metadata to the frontend.
     */
    @PostMapping("/create-order")
    @ResponseBody
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest request) {
        log.info("Received order creation request for: {}", request.getEmail());
        try {
            OrderResponse response = paymentService.createOrder(request);
            return ResponseEntity.ok(response);
        } catch (RazorpayException e) {
            log.error("Order creation failed: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to create order: " + e.getMessage()));
        }
    }

    /**
     * POST /payment/verify
     * Verifies the Razorpay payment signature returned by the Checkout JS widget.
     * Should be called immediately after a successful payment on the frontend.
     */
    @PostMapping("/verify")
    @ResponseBody
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentVerificationRequest request) {
        log.info("Received payment verification for orderId={}", request.getRazorpayOrderId());
        boolean isValid = paymentService.verifyPaymentSignature(request);

        if (isValid) {
            // ──────────────────────────────────────────────
            // TODO: Persist the payment record in your DB here
            // e.g. paymentRepository.save(buildPaymentEntity(request));
            // ──────────────────────────────────────────────
            log.info("Payment verified successfully: paymentId={}", request.getRazorpayPaymentId());
            return ResponseEntity.ok(Map.of(
                    "status",    "success",
                    "message",   "Payment verified successfully",
                    "paymentId", request.getRazorpayPaymentId(),
                    "orderId",   request.getRazorpayOrderId()
            ));
        } else {
            log.warn("Payment verification failed for orderId={}", request.getRazorpayOrderId());
            return ResponseEntity.badRequest().body(Map.of(
                    "status",  "failure",
                    "message", "Payment signature verification failed. Please contact support."
            ));
        }
    }

    /**
     * GET /payment/details/{paymentId}
     * Fetches raw payment details from Razorpay (useful for admin/debug).
     */
    @GetMapping("/details/{paymentId}")
    @ResponseBody
    public ResponseEntity<String> getPaymentDetails(@PathVariable String paymentId) {
        String details = paymentService.fetchPaymentDetails(paymentId);
        return ResponseEntity.ok(details);
    }
}
