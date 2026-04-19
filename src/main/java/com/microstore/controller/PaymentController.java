package com.microstore.controller;

import com.microstore.dto.ApiResponse;
import com.microstore.service.OrderService;
import com.microstore.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * PaymentController - REST API for Razorpay payment processing.
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderService orderService;

    /** Create Razorpay order for payment */
    @PostMapping("/create/{orderId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createPayment(@PathVariable Long orderId) {
        var order = orderService.findById(orderId)
                .orElseThrow(() -> new com.microstore.exception.ResourceNotFoundException("Order", "id", orderId));

        Map<String, Object> result = paymentService.createRazorpayOrder(orderId, order.getTotalAmount());

        if ((boolean) result.get("success")) {
            return ResponseEntity.ok(ApiResponse.success("Payment order created", result));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error(result.get("error").toString()));
        }
    }

    /** Verify payment after Razorpay checkout */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyPayment(@RequestBody Map<String, String> paymentData) {
        String razorpayOrderId = paymentData.get("razorpay_order_id");
        String razorpayPaymentId = paymentData.get("razorpay_payment_id");
        String razorpaySignature = paymentData.get("razorpay_signature");
        Long orderId = Long.valueOf(paymentData.get("order_id"));

        boolean verified = paymentService.verifyPayment(razorpayOrderId, razorpayPaymentId, razorpaySignature);

        if (verified) {
            orderService.updatePaymentStatus(orderId, "PAID", razorpayPaymentId);
            return ResponseEntity.ok(ApiResponse.success("Payment verified successfully"));
        } else {
            orderService.updatePaymentStatus(orderId, "FAILED", null);
            return ResponseEntity.badRequest().body(ApiResponse.error("Payment verification failed"));
        }
    }
}
