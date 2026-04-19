package com.microstore.service;

import com.microstore.model.CartItem;
import com.microstore.model.Product;
import com.microstore.repository.CartItemRepository;
import com.microstore.repository.ProductRepository;
import com.microstore.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * CartService - Business logic for Shopping Cart.
 * Demonstrates: Collections (ArrayList, HashMap), Encapsulation, Exception Handling
 */
@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Add item to cart. If already exists, increment quantity.
     */
    public CartItem addToCart(Long userId, Long productId, int quantity, String size) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (!product.isInStock(quantity)) {
            throw new IllegalArgumentException("Insufficient stock for " + product.getName());
        }

        // Check if item already in cart
        Optional<CartItem> existing = cartItemRepository.findByUserIdAndProductIdAndSelectedSize(userId, productId, size);
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
            return cartItemRepository.save(item);
        }

        CartItem cartItem = new CartItem(userId, product, quantity, size);
        return cartItemRepository.save(cartItem);
    }

    /**
     * Get all cart items for a user.
     */
    public List<CartItem> getCartItems(Long userId) {
        return cartItemRepository.findByUserId(userId);
    }

    /**
     * Update cart item quantity.
     */
    public CartItem updateQuantity(Long cartItemId, int quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        if (quantity <= 0) {
            cartItemRepository.delete(item);
            return null;
        }

        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    /**
     * Remove item from cart.
     */
    public void removeFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    /**
     * Clear entire cart for a user.
     */
    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    /**
     * Get cart summary using HashMap - demonstrates collection usage.
     */
    public Map<String, Object> getCartSummary(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        Map<String, Object> summary = new HashMap<>();

        double subtotal = 0;
        double totalDiscount = 0;
        int totalItems = 0;

        // Using ArrayList to store item details
        List<Map<String, Object>> itemDetails = new ArrayList<>();

        for (CartItem item : items) {
            Product product = item.getProduct();
            double itemTotal = product.getEffectivePrice() * item.getQuantity();
            double itemDiscount = (product.getPrice() - product.getEffectivePrice()) * item.getQuantity();

            subtotal += itemTotal;
            totalDiscount += itemDiscount;
            totalItems += item.getQuantity();

            Map<String, Object> detail = new HashMap<>();
            detail.put("id", item.getId());
            detail.put("productId", product.getId());
            detail.put("name", product.getName());
            detail.put("brand", product.getBrand());
            detail.put("image", product.getImageUrl());
            detail.put("price", product.getPrice());
            detail.put("discountPrice", product.getEffectivePrice());
            detail.put("quantity", item.getQuantity());
            detail.put("size", item.getSelectedSize());
            detail.put("subtotal", itemTotal);
            itemDetails.add(detail);
        }

        summary.put("items", itemDetails);
        summary.put("itemCount", totalItems);
        summary.put("subtotal", subtotal);
        summary.put("discount", totalDiscount);
        summary.put("deliveryFee", subtotal > 999 ? 0 : 99);
        summary.put("total", subtotal + (subtotal > 999 ? 0 : 99));

        return summary;
    }

    public long getCartCount(Long userId) {
        return cartItemRepository.countByUserId(userId);
    }
}
