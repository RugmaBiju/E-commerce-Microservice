package com.microstore.controller;

import com.microstore.dto.ApiResponse;
import com.microstore.model.Category;
import com.microstore.repository.CategoryRepository;
import com.microstore.service.OrderService;
import com.microstore.service.ProductService;
import com.microstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AdminController - Admin dashboard API endpoints.
 * Provides statistics, category management, and system overview.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CategoryRepository categoryRepository;

    /** Get dashboard statistics */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("users", userService.getUserStats());
        dashboard.put("products", productService.getProductStats());
        dashboard.put("orders", orderService.getOrderStats());
        return ResponseEntity.ok(ApiResponse.success("Dashboard data", dashboard));
    }

    /** Get all categories */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<Category>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success("Categories", categoryRepository.findAll()));
    }

    /** Create category */
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<Category>> createCategory(@RequestBody Category category) {
        return ResponseEntity.ok(ApiResponse.success("Category created", categoryRepository.save(category)));
    }

    /** Update category */
    @PutMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<Category>> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new com.microstore.exception.ResourceNotFoundException("Category", "id", id));
        if (category.getName() != null) existing.setName(category.getName());
        if (category.getDescription() != null) existing.setDescription(category.getDescription());
        if (category.getImageUrl() != null) existing.setImageUrl(category.getImageUrl());
        return ResponseEntity.ok(ApiResponse.success("Category updated", categoryRepository.save(existing)));
    }

    /** Delete category */
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted"));
    }
}
