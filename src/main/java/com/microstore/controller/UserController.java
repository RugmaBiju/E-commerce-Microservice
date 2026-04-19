package com.microstore.controller;

import com.microstore.dto.ApiResponse;
import com.microstore.model.User;
import com.microstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * UserController - REST API for User management (Presentation Layer).
 * Provides endpoints for registration, login, CRUD operations.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /** Register a new user */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody User user) {
        User created = userService.create(user);
        created.setPassword(null); // Don't expose password
        return ResponseEntity.ok(ApiResponse.success("Registration successful", created));
    }

    /** Login */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<User>> login(@RequestBody Map<String, String> credentials) {
        User user = userService.authenticate(credentials);
        user.setPassword(null);
        return ResponseEntity.ok(ApiResponse.success("Login successful", user));
    }

    /** Get user by ID */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable Long id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new com.microstore.exception.ResourceNotFoundException("User", "id", id));
        user.setPassword(null);
        return ResponseEntity.ok(ApiResponse.success("User found", user));
    }

    /** Get all users */
    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.findAll();
        users.forEach(u -> u.setPassword(null));
        return ResponseEntity.ok(ApiResponse.success("Users retrieved", users));
    }

    /** Update user */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updated = userService.update(id, user);
        updated.setPassword(null);
        return ResponseEntity.ok(ApiResponse.success("User updated", updated));
    }

    /** Delete user */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted"));
    }

    /** Search users */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<User>>> searchUsers(@RequestParam String q) {
        List<User> users = userService.search(q);
        users.forEach(u -> u.setPassword(null));
        return ResponseEntity.ok(ApiResponse.success("Search results", users));
    }
}
