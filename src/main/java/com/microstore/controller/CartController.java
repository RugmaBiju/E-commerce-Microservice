package com.microstore.controller;

import com.microstore.dto.ApiResponse;
import com.microstore.model.CartItem;
import com.microstore.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * CartController - REST API for Shopping Cart operations.
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /** Add item to cart */
    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<CartItem>> addToCart(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> request) {
        Long productId = Long.valueOf(request.get("productId").toString());
        int quantity = request.containsKey("quantity") ? Integer.parseInt(request.get("quantity").toString()) : 1;
        String size = request.containsKey("size") ? request.get("size").toString() : "M";

        CartItem item = cartService.addToCart(userId, productId, quantity, size);
        return ResponseEntity.ok(ApiResponse.success("Added to cart", item));
    }

    /** Get cart items */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<CartItem>>> getCartItems(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Cart items", cartService.getCartItems(userId)));
    }

    /** Get cart summary with totals */
    @GetMapping("/{userId}/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCartSummary(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Cart summary", cartService.getCartSummary(userId)));
    }

    /** Update cart item quantity */
    @PutMapping("/item/{cartItemId}")
    public ResponseEntity<ApiResponse<CartItem>> updateQuantity(
            @PathVariable Long cartItemId,
            @RequestBody Map<String, Integer> request) {
        CartItem updated = cartService.updateQuantity(cartItemId, request.get("quantity"));
        return ResponseEntity.ok(ApiResponse.success("Cart updated", updated));
    }

    /** Remove item from cart */
    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(@PathVariable Long cartItemId) {
        cartService.removeFromCart(cartItemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart"));
    }

    /** Clear cart */
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared"));
    }

    /** Get cart count */
    @GetMapping("/{userId}/count")
    public ResponseEntity<ApiResponse<Long>> getCartCount(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Cart count", cartService.getCartCount(userId)));
    }
}
