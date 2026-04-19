package com.microstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


@Entity
@Table(name = "users")
public class User extends BaseEntity implements Searchable {

    // ========================
    // PRIVATE FIELDS (Encapsulation)
    // ========================

    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    @Column(name = "phone")
    private String phone;

    @Column(name = "role")
    private String role = "USER"; // USER or ADMIN

    @Column(name = "avatar_url")
    private String avatarUrl;

    // ========================
    // CONSTRUCTORS (Overloading)
    // ========================

    /** Default constructor */
    public User() {
        super();
    }

    /** Constructor with email and password - for quick login */
    public User(String email, String password) {
        super();
        this.email = email;
        this.password = password;
    }

    /** Constructor with basic info */
    public User(String firstName, String lastName, String email, String password) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    /** Full constructor with all fields */
    public User(String firstName, String lastName, String email, String password, String phone, String role) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
    }

    // ========================
    // OVERRIDDEN ABSTRACT METHOD (Polymorphism)
    // ========================

    @Override
    public String getEntityType() {
        return "USER";
    }

    @Override
    public String getDisplayInfo() {
        return firstName + " " + lastName + " (" + email + ")";
    }

    // ========================
    // INTERFACE IMPLEMENTATION (Searchable)
    // ========================

    @Override
    public boolean matchesSearchQuery(String query) {
        if (query == null || query.trim().isEmpty()) return true;
        String lowerQuery = query.toLowerCase();
        return getSearchableText().toLowerCase().contains(lowerQuery);
    }

    @Override
    public String getSearchableText() {
        return String.join(" ", firstName, lastName, email, phone != null ? phone : "");
    }

    // ========================
    // HELPER METHODS
    // ========================

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    // ========================
    // GETTERS AND SETTERS (Encapsulation)
    // ========================

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    // ========================
    // OVERRIDDEN METHODS
    // ========================

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
