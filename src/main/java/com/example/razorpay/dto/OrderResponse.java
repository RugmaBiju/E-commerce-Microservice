package com.example.razorpay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO sent back to the frontend after a Razorpay order is created.
 * Contains the orderId and key needed to open the Razorpay checkout.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    /** Razorpay order ID (prefix: order_) */
    private String orderId;

    /** Razorpay Key ID (safe to expose to frontend) */
    private String razorpayKeyId;

    /** Amount in paise */
    private Long amount;

    /** Currency code */
    private String currency;

    /** Description / product name */
    private String description;

    /** Customer name */
    private String name;

    /** Customer email */
    private String email;

    /** Customer phone */
    private String phone;

    /** Application callback URL after payment */
    private String callbackUrl;
}
