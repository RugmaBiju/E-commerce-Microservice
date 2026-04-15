package com.example.razorpay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO after payment verification is successful.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerificationResponse {

    /** Status of verification: success or failure */
    private String status;

    /** Human-readable message */
    private String message;

    /** Razorpay Payment ID */
    private String paymentId;

    /** Razorpay Order ID */
    private String orderId;

    /** Amount paid in paise */
    private Long amount;

    /** Currency code */
    private String currency;

    /** Payment method used */
    private String paymentMethod;

    /** Customer email */
    private String email;

    /** Whether signature is verified */
    private Boolean verified;

    /** Timestamp of verification */
    private Long timestamp;
}
