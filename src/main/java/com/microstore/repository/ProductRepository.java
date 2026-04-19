package com.microstore.repository;

import com.microstore.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ProductRepository - DAO for Product entity.
 * Provides CRUD + custom query methods for product management.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /** Find products by category */
    List<Product> findByCategoryId(Long categoryId);

    /** Find products by brand */
    List<Product> findByBrand(String brand);

    /** Find featured products */
    List<Product> findByFeaturedTrue();

    /** Find by gender */
    List<Product> findByGender(String gender);

    /** Find by gender and category */
    List<Product> findByGenderAndCategoryId(String gender, Long categoryId);

    /** Search products by name, brand, description */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.color) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> searchProducts(@Param("query") String query);

    /** Find products within a price range */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceRange(@Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice);

    /** Find products with discount */
    @Query("SELECT p FROM Product p WHERE p.discountPrice > 0 AND p.discountPrice < p.price")
    List<Product> findDiscountedProducts();

    /** Get distinct brands */
    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.brand IS NOT NULL ORDER BY p.brand")
    List<String> findDistinctBrands();

    /** Get distinct colors */
    @Query("SELECT DISTINCT p.color FROM Product p WHERE p.color IS NOT NULL ORDER BY p.color")
    List<String> findDistinctColors();
}
