package com.microstore.model;

import jakarta.persistence.*;

/**
 * Address Entity - Represents a user's shipping address.
 * Demonstrates: Inheritance, Encapsulation, Method Overriding
 */
@Entity
@Table(name = "addresses")
public class Address extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "street")
    private String street;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "country")
    private String country = "India";

    @Column(name = "is_default")
    private boolean isDefault = false;

    // Constructors
    public Address() {
        super();
    }

    public Address(Long userId, String fullName, String street, String city, String state, String zipCode) {
        super();
        this.userId = userId;
        this.fullName = fullName;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    @Override
    public String getEntityType() {
        return "ADDRESS";
    }

    @Override
    public String getDisplayInfo() {
        return fullName + ", " + street + ", " + city + " - " + zipCode;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    @Override
    public String toString() {
        return "Address{id=" + getId() + ", city='" + city + "', state='" + state + "'}";
    }
}
