package com.microstore.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * BaseEntity - Abstract base class for all domain entities.
 * 
 * OOP Concepts Demonstrated:
 * - ABSTRACTION: Abstract class with abstract method getEntityType()
 * - ENCAPSULATION: Private fields with public getters/setters
 * - METHOD OVERLOADING: Multiple versions of getDisplayInfo()
 * - CONSTRUCTOR USAGE: Default and parameterized constructors
 * - METHOD OVERRIDING: toString(), equals(), hashCode()
 */
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========================
    // CONSTRUCTORS (Overloading)
    // ========================

    /** Default constructor */
    public BaseEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /** Parameterized constructor - demonstrates constructor overloading */
    public BaseEntity(Long id) {
        this();
        this.id = id;
    }

    // ========================
    // ABSTRACT METHOD (Abstraction)
    // ========================

    /**
     * Returns the type of entity - must be implemented by subclasses.
     * Demonstrates abstraction and polymorphism.
     */
    public abstract String getEntityType();

    // ========================
    // METHOD OVERLOADING
    // ========================

    /**
     * Returns basic display information about the entity.
     * Can be overridden by subclasses for custom display.
     */
    public String getDisplayInfo() {
        return getEntityType() + " #" + id;
    }

    /**
     * Overloaded version - returns detailed or basic display info.
     * Demonstrates method overloading.
     */
    public String getDisplayInfo(boolean detailed) {
        if (detailed) {
            return getEntityType() + " #" + id + " [Created: " + createdAt + ", Updated: " + updatedAt + "]";
        }
        return getDisplayInfo();
    }

    /**
     * Overloaded version - returns display info with a custom prefix.
     * Demonstrates method overloading with different parameter types.
     */
    public String getDisplayInfo(String prefix) {
        return prefix + " " + getDisplayInfo();
    }

    // ========================
    // JPA LIFECYCLE CALLBACKS
    // ========================

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ========================
    // GETTERS AND SETTERS (Encapsulation)
    // ========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ========================
    // OVERRIDDEN METHODS (Polymorphism)
    // ========================

    @Override
    public String toString() {
        return getEntityType() + "{id=" + id + ", createdAt=" + createdAt + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
