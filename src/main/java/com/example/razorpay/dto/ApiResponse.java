package com.example.razorpay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic API response wrapper for all REST endpoints.
 * Provides consistent response format across the application.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /** Response status: success, error, warning */
    private String status;

    /** Human-readable message */
    private String message;

    /** Response data/payload */
    private T data;

    /** Error code (for debugging) */
    private String errorCode;

    /** Timestamp of response */
    private Long timestamp;

    /**
     * Factory method for success response
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status("success")
                .message(message)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * Factory method for error response
     */
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return ApiResponse.<T>builder()
                .status("error")
                .message(message)
                .errorCode(errorCode)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * Factory method for error response with data
     */
    public static <T> ApiResponse<T> error(String message, String errorCode, T data) {
        return ApiResponse.<T>builder()
                .status("error")
                .message(message)
                .errorCode(errorCode)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
