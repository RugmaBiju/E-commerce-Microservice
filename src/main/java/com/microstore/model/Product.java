package com.microstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Product Entity - Core entity representing a fashion product.
 * 
 * OOP Concepts Demonstrated:
 * - INHERITANCE: Extends BaseEntity
 * - INTERFACE: Implements Searchable
 * - ENCAPSULATION: Private fields, public getters/setters
 * - METHOD OVERRIDING: getEntityType(), getDisplayInfo(), toString()
 * - METHOD OVERLOADING: Multiple constructors
 * - COLLECTIONS: Used in relationships (via JPA)
 */
@Entity
@Table(name = "products")
public class Product extends BaseEntity implements Searchable {

    @NotBlank(message = "Product name is required")
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Positive(message = "Price must be positive")
    @Column(nullable = false)
    private double price;

    @Column(name = "discount_price")
    private double discountPrice;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(name = "image_url_2", length = 1000)
    private String imageUrl2;

    @Column(name = "image_url_3", length = 1000)
    private String imageUrl3;

    @Column(name = "stock_quantity")
    private int stockQuantity = 0;

    @Column(name = "brand")
    private String brand;

    @Column(name = "sizes")
    private String sizes; // Comma-separated: "S,M,L,XL"

    @Column(name = "color")
    private String color;

    @Column(name = "material")
    private String material;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "rating")
    private double rating = 0.0;

    @Column(name = "review_count")
    private int reviewCount = 0;

    @Column(name = "is_featured")
    private boolean featured = false;

    @Column(name = "gender")
    private String gender; // MEN, WOMEN, UNISEX, KIDS

    // ========================
    // CONSTRUCTORS (Overloading)
    // ========================

    public Product() {
        super();
    }

    public Product(String name, double price) {
        super();
        this.name = name;
        this.price = price;
    }

    public Product(String name, String description, double price, String brand) {
        super();
        this.name = name;
        this.description = description;
        this.price = price;
        this.brand = brand;
    }

    public Product(String name, String description, double price, double discountPrice,
                   String imageUrl, int stockQuantity, String brand, String sizes,
                   String color, Category category) {
        super();
        this.name = name;
        this.description = description;
        this.price = price;
        this.discountPrice = discountPrice;
        this.imageUrl = imageUrl;
        this.stockQuantity = stockQuantity;
        this.brand = brand;
        this.sizes = sizes;
        this.color = color;
        this.category = category;
    }

    // ========================
    // OVERRIDDEN METHODS (Polymorphism)
    // ========================

    @Override
    public String getEntityType() {
        return "PRODUCT";
    }

    @Override
    public String getDisplayInfo() {
        return brand + " - " + name + " (₹" + getEffectivePrice() + ")";
    }

    // ========================
    // SEARCHABLE IMPLEMENTATION
    // ========================

    @Override
    public boolean matchesSearchQuery(String query) {
        if (query == null || query.trim().isEmpty()) return true;
        return getSearchableText().toLowerCase().contains(query.toLowerCase());
    }

    @Override
    public String getSearchableText() {
        StringBuilder sb = new StringBuilder();
        sb.append(name != null ? name : "").append(" ");
        sb.append(brand != null ? brand : "").append(" ");
        sb.append(description != null ? description : "").append(" ");
        sb.append(color != null ? color : "").append(" ");
        sb.append(material != null ? material : "").append(" ");
        sb.append(gender != null ? gender : "");
        if (category != null) {
            sb.append(" ").append(category.getName());
        }
        return sb.toString();
    }

    // ========================
    // BUSINESS LOGIC METHODS
    // ========================

    /**
     * Returns the effective price considering discounts.
     */
    public double getEffectivePrice() {
        return discountPrice > 0 ? discountPrice : price;
    }

    /**
     * Calculates discount percentage.
     */
    public int getDiscountPercentage() {
        if (discountPrice > 0 && price > 0) {
            return (int) Math.round(((price - discountPrice) / price) * 100);
        }
        return 0;
    }

    /**
     * Checks if the product is in stock.
     */
    public boolean isInStock() {
        return stockQuantity > 0;
    }

    /**
     * Checks if the product is in stock for a given quantity.
     * Demonstrates method overloading.
     */
    public boolean isInStock(int requiredQuantity) {
        return stockQuantity >= requiredQuantity;
    }

    // ========================
    // GETTERS AND SETTERS (Encapsulation)
    // ========================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl2() {
        return imageUrl2;
    }

    public void setImageUrl2(String imageUrl2) {
        this.imageUrl2 = imageUrl2;
    }

    public String getImageUrl3() {
        return imageUrl3;
    }

    public void setImageUrl3(String imageUrl3) {
        this.imageUrl3 = imageUrl3;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSizes() {
        return sizes;
    }

    public void setSizes(String sizes) {
        this.sizes = sizes;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", price=" + price +
                ", discountPrice=" + discountPrice +
                ", stock=" + stockQuantity +
                '}';
    }
}
