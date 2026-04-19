package com.microstore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * OrderItem Entity - Represents a single item within an order.
 * Demonstrates: Inheritance, Encapsulation, Relationships
 */
@Entity
@Table(name = "order_items")
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_brand")
    private String productBrand;

    @Column(name = "product_image")
    private String productImage;

    @Column(name = "quantity")
    private int quantity = 1;

    @Column(name = "price")
    private double price;

    @Column(name = "selected_size")
    private String selectedSize;

    // Constructors
    public OrderItem() {
        super();
    }

    public OrderItem(Order order, Long productId, String productName, int quantity, double price) {
        super();
        this.order = order;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    @Override
    public String getEntityType() {
        return "ORDER_ITEM";
    }

    @Override
    public String getDisplayInfo() {
        return productName + " x" + quantity + " @ ₹" + price;
    }

    /**
     * Calculates the subtotal for this order item.
     */
    public double getSubtotal() {
        return price * quantity;
    }

    // Getters and Setters
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductBrand() { return productBrand; }
    public void setProductBrand(String productBrand) { this.productBrand = productBrand; }
    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getSelectedSize() { return selectedSize; }
    public void setSelectedSize(String selectedSize) { this.selectedSize = selectedSize; }

    @Override
    public String toString() {
        return "OrderItem{productName='" + productName + "', qty=" + quantity + ", price=" + price + "}";
    }
}
