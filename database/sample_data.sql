-- ============================================
-- MicroStore - Sample Data Records
-- Insert after running schema.sql
-- ============================================

USE microstore_db;

-- ====== SAMPLE USERS ======
INSERT INTO users (first_name, last_name, email, password, phone, role) VALUES
('Admin', 'User', 'admin@microstore.com', 'admin123', '9876543210', 'ADMIN'),
('Rahul', 'Sharma', 'rahul@email.com', 'password123', '9876543211', 'USER'),
('Priya', 'Patel', 'priya@email.com', 'password123', '9876543212', 'USER'),
('Amit', 'Kumar', 'amit@email.com', 'password123', '9876543213', 'USER');

-- ====== SAMPLE CATEGORIES ======
INSERT INTO categories (name, description, image_url) VALUES
('Men', 'Men''s Fashion Collection', 'https://images.unsplash.com/photo-1490578474895-699cd4e2cf59?w=400'),
('Women', 'Women''s Fashion Collection', 'https://images.unsplash.com/photo-1483985988355-763728e1935b?w=400'),
('Kids', 'Kids'' Fashion Collection', 'https://images.unsplash.com/photo-1519238263530-99bdd11df2ea?w=400'),
('Footwear', 'Footwear Collection', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400'),
('Accessories', 'Fashion Accessories', 'https://images.unsplash.com/photo-1523170335258-f5ed11844a49?w=400');

-- ====== SAMPLE PRODUCTS ======
INSERT INTO products (name, description, price, discount_price, image_url, stock_quantity, brand, sizes, color, material, category_id, rating, review_count, is_featured, gender) VALUES
('Classic Slim Fit Cotton Shirt', 'Premium cotton slim fit formal shirt with button-down collar.', 1999, 1299, 'https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=500', 50, 'Allen Solly', 'S,M,L,XL,XXL', 'White', 'Cotton', 1, 4.3, 245, TRUE, 'MEN'),
('Tapered Fit Stretch Jeans', 'Comfortable stretch denim jeans with tapered fit.', 2499, 1799, 'https://images.unsplash.com/photo-1542272454315-4c01d7abdf4a?w=500', 35, 'Levi''s', '28,30,32,34,36', 'Blue', 'Denim', 1, 4.5, 890, TRUE, 'MEN'),
('Printed Casual T-Shirt', 'Trendy graphic printed round neck t-shirt.', 999, 699, 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=500', 100, 'H&M', 'S,M,L,XL', 'Black', 'Cotton Blend', 1, 4.1, 1567, FALSE, 'MEN'),
('Floral Print Maxi Dress', 'Elegant floral maxi dress with sweetheart neckline.', 3499, 2199, 'https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=500', 30, 'ZARA', 'XS,S,M,L,XL', 'Floral Pink', 'Georgette', 2, 4.7, 567, TRUE, 'WOMEN'),
('High-Rise Skinny Jeans', 'Ultra-stretch high-rise skinny jeans.', 2299, 1599, 'https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=500', 60, 'Levi''s', '26,28,30,32', 'Dark Blue', 'Stretch Denim', 2, 4.5, 1234, TRUE, 'WOMEN'),
('Running Performance Shoes', 'Lightweight running shoes with responsive cushioning.', 5999, 4499, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500', 40, 'Nike', '7,8,9,10,11', 'Red/Black', 'Mesh/Rubber', 4, 4.7, 1890, TRUE, 'UNISEX'),
('Chronograph Analog Watch', 'Sophisticated chronograph watch with stainless steel case.', 7999, 5499, 'https://images.unsplash.com/photo-1523170335258-f5ed11844a49?w=500', 15, 'Fossil', 'Free Size', 'Silver/Brown', 'Stainless Steel/Leather', 5, 4.6, 567, TRUE, 'MEN'),
('Aviator Sunglasses UV Protection', 'Classic aviator sunglasses with UV400 protection.', 2999, 1999, 'https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=500', 50, 'Ray-Ban', 'Free Size', 'Gold/Green', 'Metal/Glass', 5, 4.8, 1345, TRUE, 'UNISEX');

-- ====== SAMPLE ADDRESS ======
INSERT INTO addresses (user_id, full_name, phone, street, city, state, zip_code, is_default) VALUES
(2, 'Rahul Sharma', '9876543211', '42 MG Road, Sector 15', 'Bangalore', 'Karnataka', '560001', TRUE);

-- ====== SAMPLE ORDER ======
INSERT INTO orders (user_id, total_amount, status, payment_status, shipping_name, shipping_street, shipping_city, shipping_state, shipping_zip, shipping_phone) VALUES
(2, 3098, 'DELIVERED', 'PAID', 'Rahul Sharma', '42 MG Road, Sector 15', 'Bangalore', 'Karnataka', '560001', '9876543211');

INSERT INTO order_items (order_id, product_id, product_name, product_brand, quantity, price, selected_size) VALUES
(1, 1, 'Classic Slim Fit Cotton Shirt', 'Allen Solly', 1, 1299, 'M'),
(1, 3, 'Printed Casual T-Shirt', 'H&M', 1, 699, 'L');
