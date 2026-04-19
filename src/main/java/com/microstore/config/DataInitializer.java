package com.microstore.config;

import com.microstore.model.*;
import com.microstore.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * DataInitializer - Seeds the database with sample fashion products on startup.
 * Only runs if database is empty.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            System.out.println("Database already initialized, skipping seed data.");
            return;
        }

        System.out.println("Initializing database with sample data...");

        // ====== Create Categories ======
        Category menCategory = createCategory("Men", "Men's Fashion Collection",
                "https://images.unsplash.com/photo-1490578474895-699cd4e2cf59?w=400");
        Category womenCategory = createCategory("Women", "Women's Fashion Collection",
                "https://images.unsplash.com/photo-1483985988355-763728e1935b?w=400");
        Category kidsCategory = createCategory("Kids", "Kids' Fashion Collection",
                "https://images.unsplash.com/photo-1519238263530-99bdd11df2ea?w=400");
        Category footwearCategory = createCategory("Footwear", "Footwear Collection",
                "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400");
        Category accessoriesCategory = createCategory("Accessories", "Fashion Accessories",
                "https://images.unsplash.com/photo-1523170335258-f5ed11844a49?w=400");

        // ====== Create Admin User ======
        User admin = new User("Admin", "User", "admin@microstore.com", "admin123", "9876543210", "ADMIN");
        userRepository.save(admin);

        // ====== Create Sample Customer ======
        User customer = new User("Rahul", "Sharma", "rahul@email.com", "password123", "9876543211", "USER");
        userRepository.save(customer);

        // ====== Create Men's Products ======
        createProduct("Classic Slim Fit Cotton Shirt", "Premium cotton slim fit formal shirt with button-down collar. Perfect for office and casual wear.",
                1999, 1299, "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=500",
                "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=500",
                "https://images.unsplash.com/photo-1598033129183-c4f50c736c10?w=500",
                50, "Allen Solly", "S,M,L,XL,XXL", "White", "Cotton", menCategory, 4.3, 245, true, "MEN");

        createProduct("Tapered Fit Stretch Jeans", "Comfortable stretch denim jeans with tapered fit. Fashionable and durable for everyday wear.",
                2499, 1799, "https://images.unsplash.com/photo-1542272454315-4c01d7abdf4a?w=500",
                "https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=500", null,
                35, "Levi's", "28,30,32,34,36", "Blue", "Denim", menCategory, 4.5, 890, true, "MEN");

        createProduct("Printed Casual T-Shirt", "Trendy graphic printed round neck t-shirt made from soft cotton blend fabric.",
                999, 699, "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=500",
                "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=500", null,
                100, "H&M", "S,M,L,XL", "Black", "Cotton Blend", menCategory, 4.1, 1567, false, "MEN");

        createProduct("Formal Blazer Slim Fit", "Sophisticated single-breasted blazer in premium polyester blend. Perfect for business meetings.",
                5999, 3999, "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500",
                "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=500", null,
                20, "Van Heusen", "38,40,42,44", "Navy Blue", "Polyester Blend", menCategory, 4.6, 123, true, "MEN");

        createProduct("Sports Performance Polo", "Moisture-wicking sports polo shirt with mesh ventilation panels.",
                1499, 999, "https://images.unsplash.com/photo-1586363104862-3a5e2ab60d99?w=500", null, null,
                80, "Nike", "S,M,L,XL,XXL", "Red", "Polyester", menCategory, 4.4, 432, false, "MEN");

        createProduct("Chino Casual Trousers", "Classic chino trousers in comfortable cotton twill. Versatile for work and weekends.",
                1899, 1399, "https://images.unsplash.com/photo-1473966968600-fa801b869a1a?w=500", null, null,
                45, "U.S. Polo", "30,32,34,36", "Khaki", "Cotton Twill", menCategory, 4.2, 678, false, "MEN");

        // ====== Create Women's Products ======
        createProduct("Floral Print Maxi Dress", "Elegant floral maxi dress with sweetheart neckline and flowing silhouette. Perfect for summer occasions.",
                3499, 2199, "https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=500",
                "https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=500",
                "https://images.unsplash.com/photo-1496747611176-843222e1e57c?w=500",
                30, "ZARA", "XS,S,M,L,XL", "Floral Pink", "Georgette", womenCategory, 4.7, 567, true, "WOMEN");

        createProduct("High-Rise Skinny Jeans", "Ultra-stretch high-rise skinny jeans with ankle-length cut. Maximum comfort with stylish look.",
                2299, 1599, "https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=500",
                "https://images.unsplash.com/photo-1475178626620-a4d074967f8c?w=500", null,
                60, "Levi's", "26,28,30,32", "Dark Blue", "Stretch Denim", womenCategory, 4.5, 1234, true, "WOMEN");

        createProduct("Embroidered Anarkali Kurta", "Beautiful embroidered Anarkali kurta in premium fabric. Ideal for festive occasions.",
                2799, 1899, "https://images.unsplash.com/photo-1583391733956-6c78276477e2?w=500",
                "https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=500", null,
                25, "W for Woman", "S,M,L,XL", "Maroon", "Rayon", womenCategory, 4.8, 890, true, "WOMEN");

        createProduct("Crop Top with Ruffle Detail", "Trendy crop top featuring ruffle details and adjustable straps. Perfect for casual outings.",
                1299, 799, "https://images.unsplash.com/photo-1594223274512-ad4803739b7c?w=500", null, null,
                75, "Forever 21", "XS,S,M,L", "White", "Cotton", womenCategory, 4.0, 345, false, "WOMEN");

        createProduct("Pleated Midi Skirt", "Elegant pleated midi skirt in satin finish. Pairs beautifully with blouses and tops.",
                1999, 1499, "https://images.unsplash.com/photo-1583496661160-fb5886a0aaaa?w=500", null, null,
                40, "Mango", "XS,S,M,L,XL", "Emerald Green", "Satin", womenCategory, 4.3, 234, false, "WOMEN");

        createProduct("Structured Blazer for Women", "Tailored blazer with structured shoulders and notch lapel. Professional and chic.",
                4999, 3499, "https://images.unsplash.com/photo-1591369822096-ffd140ec948f?w=500", null, null,
                15, "Marks & Spencer", "S,M,L,XL", "Black", "Polyester Wool Blend", womenCategory, 4.6, 167, false, "WOMEN");

        // ====== Create Kids Products ======
        createProduct("Kids Graphic Print T-Shirt", "Fun graphic printed t-shirt for kids. Soft cotton fabric, comfortable and colorful.",
                599, 399, "https://images.unsplash.com/photo-1519238263530-99bdd11df2ea?w=500", null, null,
                100, "H&M Kids", "2-3Y,4-5Y,6-7Y,8-9Y", "Yellow", "Cotton", kidsCategory, 4.4, 456, false, "KIDS");

        createProduct("Kids Denim Dungaree", "Adorable denim dungaree with adjustable straps and pockets. Durable for active kids.",
                1499, 999, "https://images.unsplash.com/photo-1543248939-4296e1fea89b?w=500", null, null,
                50, "GAP Kids", "3-4Y,5-6Y,7-8Y,9-10Y", "Light Blue", "Denim", kidsCategory, 4.5, 234, true, "KIDS");

        // ====== Create Footwear Products ======
        createProduct("Running Performance Shoes", "Lightweight running shoes with responsive cushioning and breathable mesh upper.",
                5999, 4499, "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500",
                "https://images.unsplash.com/photo-1460353581641-37baddab0fa2?w=500",
                "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=500",
                40, "Nike", "7,8,9,10,11", "Red/Black", "Mesh/Rubber", footwearCategory, 4.7, 1890, true, "UNISEX");

        createProduct("Classic Leather Sneakers", "Timeless leather sneakers with cushioned insole. Versatile for casual and semi-formal looks.",
                3999, 2999, "https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?w=500",
                "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=500", null,
                30, "Adidas", "6,7,8,9,10,11", "White", "Leather", footwearCategory, 4.5, 789, true, "UNISEX");

        createProduct("Block Heel Sandals", "Elegant block heel sandals with ankle strap. Comfortable heel height for all-day wear.",
                2499, 1799, "https://images.unsplash.com/photo-1543163521-1bf539c55dd2?w=500", null, null,
                25, "Charles & Keith", "4,5,6,7,8", "Nude", "Synthetic", footwearCategory, 4.3, 345, false, "WOMEN");

        // ====== Create Accessories Products ======
        createProduct("Chronograph Analog Watch", "Sophisticated chronograph watch with stainless steel case and leather strap.",
                7999, 5499, "https://images.unsplash.com/photo-1523170335258-f5ed11844a49?w=500",
                "https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=500", null,
                15, "Fossil", "Free Size", "Silver/Brown", "Stainless Steel/Leather", accessoriesCategory, 4.6, 567, true, "MEN");

        createProduct("Leather Tote Bag", "Spacious leather tote bag with inner pockets and zip closure. Perfect for work and travel.",
                3499, 2499, "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=500",
                "https://images.unsplash.com/photo-1590874103328-eac38a683ce7?w=500", null,
                20, "Hidesign", "Free Size", "Tan", "Genuine Leather", accessoriesCategory, 4.7, 234, false, "WOMEN");

        createProduct("Aviator Sunglasses UV Protection", "Classic aviator sunglasses with UV400 protection and polarized lenses.",
                2999, 1999, "https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=500", null, null,
                50, "Ray-Ban", "Free Size", "Gold/Green", "Metal/Glass", accessoriesCategory, 4.8, 1345, true, "UNISEX");

        System.out.println("✅ Database initialized with " + productRepository.count() + " products!");
    }

    private Category createCategory(String name, String description, String imageUrl) {
        Category category = new Category(name, description, imageUrl);
        return categoryRepository.save(category);
    }

    private void createProduct(String name, String description, double price, double discountPrice,
                                String imageUrl, String imageUrl2, String imageUrl3,
                                int stock, String brand, String sizes, String color, String material,
                                Category category, double rating, int reviewCount, boolean featured, String gender) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setDiscountPrice(discountPrice);
        product.setImageUrl(imageUrl);
        product.setImageUrl2(imageUrl2);
        product.setImageUrl3(imageUrl3);
        product.setStockQuantity(stock);
        product.setBrand(brand);
        product.setSizes(sizes);
        product.setColor(color);
        product.setMaterial(material);
        product.setCategory(category);
        product.setRating(rating);
        product.setReviewCount(reviewCount);
        product.setFeatured(featured);
        product.setGender(gender);
        productRepository.save(product);
    }
}
