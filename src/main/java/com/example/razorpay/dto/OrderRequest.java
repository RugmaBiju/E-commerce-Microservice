package com.example.razorpay.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO used by the frontend to request an order creation.
 * Amount is in the smallest currency unit (paise for INR).
 * e.g. ₹500 → amount = 50000
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    /** Customer's full name */
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    /** Customer's email address */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    /** Customer's mobile number */
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be 10-15 digits")
    private String phone;

    /** Amount in smallest currency unit (paise) */
    @NotNull(message = "Amount is required")
    @Min(value = 100, message = "Minimum amount is ₹1 (100 paise)")
    @Max(value = 999999999L, message = "Maximum amount is ₹9,999,999.99")
    private Long amount;

    /** ISO 4217 currency code (default: INR) */
    @Builder.Default
    private String currency = "INR";

    /** Short description / product name */
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
}
