package com.microstore.service;

import com.microstore.model.Payment;
import com.microstore.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * PaymentService - Razorpay payment integration.
 * Demonstrates: Exception Handling, External API integration, HashMap usage
 */
@Service
public class PaymentService {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Autowired
    private PaymentRepository paymentRepository;

    /**
     * Creates a Razorpay order for payment processing.
     */
    public Map<String, Object> createRazorpayOrder(Long orderId, double amount) {
        Map<String, Object> response = new HashMap<>();

        try {
            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", (int) (amount * 100)); // Amount in paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_" + orderId);

            Order razorpayOrder = razorpay.orders.create(orderRequest);

            // Save payment record
            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setRazorpayOrderId(razorpayOrder.get("id"));
            payment.setAmount(amount);
            payment.setCurrency("INR");
            payment.setStatus("CREATED");
            paymentRepository.save(payment);

            response.put("success", true);
            response.put("razorpayOrderId", razorpayOrder.get("id"));
            response.put("amount", (int) (amount * 100));
            response.put("currency", "INR");
            response.put("keyId", razorpayKeyId);

        } catch (RazorpayException e) {
            response.put("success", false);
            response.put("error", "Payment initialization failed: " + e.getMessage());
        }

        return response;
    }

    /**
     * Verifies Razorpay payment signature.
     */
    public boolean verifyPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        try {
            String data = razorpayOrderId + "|" + razorpayPaymentId;
            String generatedSignature = hmacSha256(data, razorpayKeySecret);

            if (generatedSignature.equals(razorpaySignature)) {
                // Update payment record
                Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                        .orElse(new Payment());
                payment.setRazorpayPaymentId(razorpayPaymentId);
                payment.setRazorpaySignature(razorpaySignature);
                payment.setStatus("CAPTURED");
                paymentRepository.save(payment);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * HMAC SHA256 signature generation for payment verification.
     */
    private String hmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public Payment findByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId).orElse(null);
    }
}
