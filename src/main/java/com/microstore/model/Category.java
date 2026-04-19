package com.microstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Category Entity - Represents product categories.
 * 
 * OOP Concepts: Inheritance (extends BaseEntity), Encapsulation, 
 * Method Overriding, Interface Implementation (Searchable)
 */
@Entity
@Table(name = "categories")
public class Category extends BaseEntity implements Searchable {

    @NotBlank(message = "Category name is required")
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "parent_id")
    private Long parentId;

    // ========================
    // CONSTRUCTORS (Overloading)
    // ========================

    public Category() {
        super();
    }

    public Category(String name) {
        super();
        this.name = name;
    }

    public Category(String name, String description, String imageUrl) {
        super();
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // ========================
    // OVERRIDDEN METHODS (Polymorphism)
    // ========================

    @Override
    public String getEntityType() {
        return "CATEGORY";
    }

    @Override
    public String getDisplayInfo() {
        return name;
    }

    @Override
    public boolean matchesSearchQuery(String query) {
        if (query == null || query.trim().isEmpty()) return true;
        return getSearchableText().toLowerCase().contains(query.toLowerCase());
    }

    @Override
    public String getSearchableText() {
        return name + " " + (description != null ? description : "");
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "Category{id=" + getId() + ", name='" + name + "'}";
    }
}
