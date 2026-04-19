package com.microstore.service;

import com.microstore.exception.ResourceNotFoundException;
import com.microstore.model.Product;
import com.microstore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ProductService - Business logic for Product management.
 * 
 * Demonstrates:
 * - INTERFACE IMPLEMENTATION: Implements BaseService<Product>
 * - COLLECTIONS: ArrayList, HashMap
 * - METHOD OVERLOADING: Multiple filter/search methods
 */
@Service
public class ProductService implements BaseService<Product> {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product create(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product update(Long id, Product updatedProduct) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (updatedProduct.getName() != null) existing.setName(updatedProduct.getName());
        if (updatedProduct.getDescription() != null) existing.setDescription(updatedProduct.getDescription());
        if (updatedProduct.getPrice() > 0) existing.setPrice(updatedProduct.getPrice());
        if (updatedProduct.getDiscountPrice() >= 0) existing.setDiscountPrice(updatedProduct.getDiscountPrice());
        if (updatedProduct.getImageUrl() != null) existing.setImageUrl(updatedProduct.getImageUrl());
        if (updatedProduct.getStockQuantity() >= 0) existing.setStockQuantity(updatedProduct.getStockQuantity());
        if (updatedProduct.getBrand() != null) existing.setBrand(updatedProduct.getBrand());
        if (updatedProduct.getSizes() != null) existing.setSizes(updatedProduct.getSizes());
        if (updatedProduct.getColor() != null) existing.setColor(updatedProduct.getColor());
        if (updatedProduct.getMaterial() != null) existing.setMaterial(updatedProduct.getMaterial());
        if (updatedProduct.getGender() != null) existing.setGender(updatedProduct.getGender());
        if (updatedProduct.getCategory() != null) existing.setCategory(updatedProduct.getCategory());
        existing.setFeatured(updatedProduct.isFeatured());
        if (updatedProduct.getRating() > 0) existing.setRating(updatedProduct.getRating());

        return productRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public List<Product> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return productRepository.findAll();
        }
        return productRepository.searchProducts(query);
    }

    @Override
    public long count() {
        return productRepository.count();
    }

    // ========================
    // FILTER METHODS (Method Overloading)
    // ========================

    /** Filter products by category */
    public List<Product> filterProducts(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    /** Filter products by gender */
    public List<Product> filterProducts(String gender) {
        return productRepository.findByGender(gender);
    }

    /** Filter products by gender and category */
    public List<Product> filterProducts(String gender, Long categoryId) {
        return productRepository.findByGenderAndCategoryId(gender, categoryId);
    }

    /** Filter products by price range */
    public List<Product> filterProducts(double minPrice, double maxPrice) {
        return productRepository.findByPriceRange(minPrice, maxPrice);
    }

    // ========================
    // ADDITIONAL METHODS
    // ========================

    public List<Product> getFeaturedProducts() {
        return productRepository.findByFeaturedTrue();
    }

    public List<Product> getDiscountedProducts() {
        return productRepository.findDiscountedProducts();
    }

    public List<Product> findByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    public List<String> getAllBrands() {
        return productRepository.findDistinctBrands();
    }

    public List<String> getAllColors() {
        return productRepository.findDistinctColors();
    }

    /**
     * Advanced filter with multiple criteria using HashMap.
     * Demonstrates use of HashMap and ArrayList collections.
     */
    public List<Product> advancedFilter(Map<String, String> filters) {
        List<Product> results = new ArrayList<>(productRepository.findAll());

        if (filters.containsKey("gender")) {
            String gender = filters.get("gender");
            results = results.stream()
                    .filter(p -> gender.equalsIgnoreCase(p.getGender()))
                    .collect(Collectors.toList());
        }
        if (filters.containsKey("brand")) {
            String brand = filters.get("brand");
            results = results.stream()
                    .filter(p -> brand.equalsIgnoreCase(p.getBrand()))
                    .collect(Collectors.toList());
        }
        if (filters.containsKey("color")) {
            String color = filters.get("color");
            results = results.stream()
                    .filter(p -> color.equalsIgnoreCase(p.getColor()))
                    .collect(Collectors.toList());
        }
        if (filters.containsKey("minPrice")) {
            double min = Double.parseDouble(filters.get("minPrice"));
            results = results.stream()
                    .filter(p -> p.getEffectivePrice() >= min)
                    .collect(Collectors.toList());
        }
        if (filters.containsKey("maxPrice")) {
            double max = Double.parseDouble(filters.get("maxPrice"));
            results = results.stream()
                    .filter(p -> p.getEffectivePrice() <= max)
                    .collect(Collectors.toList());
        }
        if (filters.containsKey("sort")) {
            String sort = filters.get("sort");
            switch (sort) {
                case "price_asc":
                    results.sort(Comparator.comparingDouble(Product::getEffectivePrice));
                    break;
                case "price_desc":
                    results.sort(Comparator.comparingDouble(Product::getEffectivePrice).reversed());
                    break;
                case "rating":
                    results.sort(Comparator.comparingDouble(Product::getRating).reversed());
                    break;
                case "newest":
                    results.sort(Comparator.comparing(Product::getCreatedAt).reversed());
                    break;
                default:
                    break;
            }
        }

        return results;
    }

    /**
     * Get product statistics - demonstrates HashMap usage.
     */
    public Map<String, Object> getProductStats() {
        Map<String, Object> stats = new HashMap<>();
        List<Product> allProducts = productRepository.findAll();
        stats.put("totalProducts", allProducts.size());
        stats.put("featuredProducts", productRepository.findByFeaturedTrue().size());
        stats.put("outOfStock", allProducts.stream().filter(p -> p.getStockQuantity() == 0).count());
        stats.put("brands", productRepository.findDistinctBrands().size());
        return stats;
    }
}
