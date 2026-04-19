package com.microstore.model;

import jakarta.persistence.*;

/**
 * CartItem Entity - Represents an item in a user's shopping cart.
 * Demonstrates: Inheritance, Encapsulation, Method Overriding
 */
@Entity
@Table(name = "cart_items")
public class CartItem extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity")
    private int quantity = 1;

    @Column(name = "selected_size")
    private String selectedSize;

    // Constructors
    public CartItem() {
        super();
    }

    public CartItem(Long userId, Product product, int quantity) {
        super();
        this.userId = userId;
        this.product = product;
        this.quantity = quantity;
    }

    public CartItem(Long userId, Product product, int quantity, String selectedSize) {
        super();
        this.userId = userId;
        this.product = product;
        this.quantity = quantity;
        this.selectedSize = selectedSize;
    }

    @Override
    public String getEntityType() {
        return "CART_ITEM";
    }

    @Override
    public String getDisplayInfo() {
        return product.getName() + " x" + quantity;
    }

    /**
     * Calculates the subtotal for this cart item.
     */
    public double getSubtotal() {
        return product.getEffectivePrice() * quantity;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getSelectedSize() { return selectedSize; }
    public void setSelectedSize(String selectedSize) { this.selectedSize = selectedSize; }

    @Override
    public String toString() {
        return "CartItem{userId=" + userId + ", product=" + (product != null ? product.getName() : "null") + ", qty=" + quantity + "}";
    }
}
