package com.microstore.controller;

import com.microstore.dto.ApiResponse;
import com.microstore.model.Product;
import com.microstore.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ProductController - REST API for Product management.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /** Get all products */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        return ResponseEntity.ok(ApiResponse.success("Products retrieved", productService.findAll()));
    }

    /** Get product by ID */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable Long id) {
        Product product = productService.findById(id)
                .orElseThrow(() -> new com.microstore.exception.ResourceNotFoundException("Product", "id", id));
        return ResponseEntity.ok(ApiResponse.success("Product found", product));
    }

    /** Create product */
    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(ApiResponse.success("Product created", productService.create(product)));
    }

    /** Update product */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(ApiResponse.success("Product updated", productService.update(id, product)));
    }

    /** Delete product */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted"));
    }

    /** Search products */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Product>>> searchProducts(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.success("Search results", productService.search(q)));
    }

    /** Get featured products */
    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<Product>>> getFeatured() {
        return ResponseEntity.ok(ApiResponse.success("Featured products", productService.getFeaturedProducts()));
    }

    /** Filter products by category */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<Product>>> getByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(ApiResponse.success("Products by category", productService.filterProducts(categoryId)));
    }

    /** Filter products by gender */
    @GetMapping("/gender/{gender}")
    public ResponseEntity<ApiResponse<List<Product>>> getByGender(@PathVariable String gender) {
        return ResponseEntity.ok(ApiResponse.success("Products by gender", productService.filterProducts(gender)));
    }

    /** Advanced filter with query params */
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<Product>>> filterProducts(@RequestParam Map<String, String> filters) {
        return ResponseEntity.ok(ApiResponse.success("Filtered products", productService.advancedFilter(filters)));
    }

    /** Get all brands */
    @GetMapping("/brands")
    public ResponseEntity<ApiResponse<List<String>>> getBrands() {
        return ResponseEntity.ok(ApiResponse.success("Brands", productService.getAllBrands()));
    }

    /** Get all colors */
    @GetMapping("/colors")
    public ResponseEntity<ApiResponse<List<String>>> getColors() {
        return ResponseEntity.ok(ApiResponse.success("Colors", productService.getAllColors()));
    }

    /** Get discounted products */
    @GetMapping("/deals")
    public ResponseEntity<ApiResponse<List<Product>>> getDeals() {
        return ResponseEntity.ok(ApiResponse.success("Deals", productService.getDiscountedProducts()));
    }
}
