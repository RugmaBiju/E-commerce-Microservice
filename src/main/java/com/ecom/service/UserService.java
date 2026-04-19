package com.microstore.service;

import com.microstore.exception.ResourceNotFoundException;
import com.microstore.model.User;
import com.microstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * UserService - Business logic layer for User management.
 * 
 * Demonstrates:
 * - INTERFACE IMPLEMENTATION: Implements BaseService<User>
 * - COLLECTIONS: Uses ArrayList and HashMap
 * - ENCAPSULATION: Private fields and methods
 * - METHOD OVERLOADING: Multiple versions of authenticate()
 */
@Service
public class UserService implements BaseService<User> {

    @Autowired
    private UserRepository userRepository;

    /** HashMap for caching user sessions - demonstrates HashMap usage */
    private final Map<String, Long> sessionCache = new HashMap<>();

    // ========================
    // CRUD OPERATIONS (BaseService Implementation)
    // ========================

    @Override
    public User create(User user) {
        // Validate email uniqueness
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + user.getEmail());
        }
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(Long id, User updatedUser) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (updatedUser.getFirstName() != null) existing.setFirstName(updatedUser.getFirstName());
        if (updatedUser.getLastName() != null) existing.setLastName(updatedUser.getLastName());
        if (updatedUser.getPhone() != null) existing.setPhone(updatedUser.getPhone());
        if (updatedUser.getAvatarUrl() != null) existing.setAvatarUrl(updatedUser.getAvatarUrl());

        return userRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public List<User> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return userRepository.findAll();
        }
        return userRepository.searchUsers(query);
    }

    @Override
    public long count() {
        return userRepository.count();
    }

    // ========================
    // AUTHENTICATION METHODS (Method Overloading)
    // ========================

    /**
     * Authenticate user with email and password.
     */
    public User authenticate(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
    }

    /**
     * Authenticate user with a login map (overloaded method).
     * Demonstrates method overloading with different parameter types.
     */
    public User authenticate(Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        return authenticate(email, password);
    }

    // ========================
    // SESSION MANAGEMENT (HashMap usage)
    // ========================

    public void createSession(String sessionToken, Long userId) {
        sessionCache.put(sessionToken, userId);
    }

    public Long getUserIdFromSession(String sessionToken) {
        return sessionCache.get(sessionToken);
    }

    public void invalidateSession(String sessionToken) {
        sessionCache.remove(sessionToken);
    }

    // ========================
    // ADDITIONAL METHODS
    // ========================

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findByRole(String role) {
        return userRepository.findByRole(role);
    }

    /**
     * Get user statistics - demonstrates HashMap usage.
     */
    public Map<String, Object> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("adminCount", userRepository.findByRole("ADMIN").size());
        stats.put("customerCount", userRepository.findByRole("USER").size());
        return stats;
    }
}
