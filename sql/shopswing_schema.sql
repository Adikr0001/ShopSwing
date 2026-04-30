-- ============================================================
-- ShopSwing E-Commerce Database Schema
-- MySQL 8.0+
-- ============================================================

-- Create and use the database
CREATE DATABASE IF NOT EXISTS shopswing_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE shopswing_db;

-- ============================================================
-- 1. CATEGORIES TABLE
-- ============================================================
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS admins;
DROP TABLE IF EXISTS users;

CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- 2. USERS TABLE
-- ============================================================
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- 3. ADMINS TABLE
-- ============================================================
CREATE TABLE admins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('super_admin', 'admin', 'moderator') DEFAULT 'admin',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- 4. PRODUCTS TABLE
-- ============================================================
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    category_id INT NOT NULL,
    price DECIMAL(12, 2) NOT NULL,
    description TEXT,
    brand VARCHAR(100),
    rating DECIMAL(2, 1) DEFAULT 0.0,
    stock INT DEFAULT 0,
    image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- 5. CART TABLE
-- ============================================================
CREATE TABLE cart (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_product (user_id, product_id),
    FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- 6. ORDERS TABLE
-- ============================================================
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    total_amount DECIMAL(12, 2) NOT NULL,
    shipping_address TEXT,
    status ENUM('Placed', 'Processing', 'Shipped', 'Delivered', 'Cancelled')
        DEFAULT 'Placed',
    payment_method VARCHAR(50) DEFAULT 'Cash on Delivery',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- 7. ORDER_ITEMS TABLE
-- ============================================================
CREATE TABLE order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    price DECIMAL(12, 2) NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;


-- ============================================================
-- SEED DATA: Categories (from existing Swing app)
-- ============================================================
INSERT INTO categories (name, description) VALUES
('Electronics', 'Smartphones, laptops, headphones, cameras & gadgets'),
('Clothing', 'Men & women fashion, shoes, accessories'),
('Books', 'Bestsellers, programming, self-help & more'),
('Home & Garden', 'Kitchen appliances, home decor, cleaning'),
('Sports', 'Fitness equipment, supplements, yoga accessories'),
('Beauty', 'Skincare, makeup, personal care products');


-- ============================================================
-- SEED DATA: Products (all 25 from existing Swing app)
-- ============================================================
INSERT INTO products (id, name, category_id, price, description, brand, rating, stock, image_url) VALUES
-- Electronics (category_id = 1)
(101, 'Samsung Galaxy S24 Ultra', 1, 89999.00, '6.8-inch QHD+ AMOLED, 200MP camera, Snapdragon 8 Gen 3', 'Samsung', 4.8, 50, 'images/samsung_s24.jpg'),
(102, 'Apple iPhone 15 Pro', 1, 99999.00, '6.1-inch Super Retina XDR, A17 Pro chip, ProRAW camera', 'Apple', 4.9, 30, 'images/iphone15.jpg'),
(103, 'Sony WH-1000XM5 Headphones', 1, 29990.00, 'Industry-leading noise cancellation, 30hr battery', 'Sony', 4.7, 80, 'images/sony_wh1000.jpg'),
(104, 'Dell XPS 15 Laptop', 1, 149999.00, '15.6-inch OLED 3.5K, Intel i9, 32GB RAM, RTX 4070', 'Dell', 4.6, 20, 'images/dell_xps15.jpg'),
(105, 'LG 55 inch OLED 4K TV', 1, 89990.00, '55-inch OLED evo, a9 AI Processor, Dolby Vision IQ', 'LG', 4.7, 15, 'images/lg_oled55.jpg'),
(106, 'BoAt Airdopes 141 TWS', 1, 1299.00, 'Up to 42 hours total playback, Beast Mode gaming', 'boAt', 4.3, 200, 'images/boat_airdopes.jpg'),
(107, 'Canon EOS R50 Camera', 1, 67990.00, '24.2MP APS-C sensor, 4K video, dual pixel AF', 'Canon', 4.6, 25, 'images/canon_r50.jpg'),
(108, 'Apple iPad Air M2', 1, 59900.00, '11-inch Liquid Retina, M2 chip, USB-C connector', 'Apple', 4.8, 40, 'images/ipad_air.jpg'),
(109, 'Mi Smart Band 8', 1, 3499.00, '1.62 AMOLED display, 16-day battery, 150+ sports modes', 'Xiaomi', 4.4, 150, 'images/mi_band8.jpg'),
(110, 'Logitech MX Master 3S', 1, 9995.00, '8K DPI sensor, MagSpeed scroll, quiet clicks', 'Logitech', 4.7, 60, 'images/logitech_mx.jpg'),

-- Clothing (category_id = 2)
(111, 'Levis 501 Original Jeans', 2, 3999.00, 'Classic straight fit, 100% cotton denim', 'Levi''s', 4.4, 100, 'images/levis_501.jpg'),
(112, 'Nike Air Max 270', 2, 9995.00, 'Max Air unit, breathable mesh upper, all-day comfort', 'Nike', 4.5, 75, 'images/nike_airmax.jpg'),
(113, 'Allen Solly Formal Shirt', 2, 2499.00, 'Slim fit, wrinkle-free fabric, button-down collar', 'Allen Solly', 4.3, 120, 'images/allen_solly.jpg'),
(114, 'H&M Oversized Hoodie', 2, 2999.00, 'Soft fleece interior, kangaroo pocket, relaxed fit', 'H&M', 4.2, 90, 'images/hm_hoodie.jpg'),

-- Books (category_id = 3)
(115, 'Atomic Habits', 3, 499.00, 'Transform your life with tiny changes - James Clear', 'James Clear', 4.9, 500, 'images/atomic_habits.jpg'),
(116, 'Clean Code', 3, 1299.00, 'A handbook of agile software craftsmanship, R.C.Martin', 'Robert C Martin', 4.8, 300, 'images/clean_code.jpg'),
(117, 'The Pragmatic Programmer', 3, 1499.00, 'Your journey to mastery, 20th anniversary edition', 'Hunt & Thomas', 4.7, 250, 'images/pragmatic_prog.jpg'),
(118, 'Deep Work by Cal Newport', 3, 599.00, 'Rules for focused success in a distracted world', 'Cal Newport', 4.6, 400, 'images/deep_work.jpg'),

-- Home & Garden (category_id = 4)
(119, 'Dyson V15 Detect Vacuum', 4, 52900.00, 'Laser dust detection, 60 min run time, HEPA filter', 'Dyson', 4.7, 18, 'images/dyson_v15.jpg'),
(120, 'Instant Pot Duo 7-in-1', 4, 12999.00, 'Pressure cooker, slow cooker, rice cooker and more', 'Instant Pot', 4.6, 45, 'images/instant_pot.jpg'),

-- Sports (category_id = 5)
(121, 'Yoga Mat Pro 6mm', 5, 1999.00, 'Non-slip surface, eco-friendly TPE, carry strap included', 'Boldfit', 4.4, 200, 'images/yoga_mat.jpg'),
(122, 'Whey Protein Gold 2 kg', 5, 3499.00, '24g protein per serving, chocolate flavour, lab tested', 'ON', 4.7, 80, 'images/whey_protein.jpg'),
(123, 'Adjustable Dumbbell 20 kg', 5, 4599.00, 'Quick-change weight system, chrome finish, anti-roll', 'PowerMax', 4.3, 35, 'images/dumbbell.jpg'),

-- Beauty (category_id = 6)
(124, 'Maybelline Fit Me Foundation', 6, 599.00, 'Natural finish, SPF 18, 40 shades available', 'Maybelline', 4.4, 300, 'images/maybelline.jpg'),
(125, 'Cetaphil Gentle Face Wash', 6, 385.00, 'Soap-free, non-comedogenic, for sensitive skin', 'Cetaphil', 4.6, 250, 'images/cetaphil.jpg'),
(126, 'Sony PlayStation 5 Slim', 1, 44990.00, 'Ultra-high speed SSD, integrated I/O, Ray Tracing', 'Sony', 4.9, 15, 'images/ps5.jpg'),
(127, 'Nintendo Switch OLED', 1, 32990.00, '7-inch OLED screen, wide adjustable stand, wired LAN port', 'Nintendo', 4.8, 20, 'images/switch_oled.jpg'),
(128, 'Bose QC Ultra Headphones', 1, 35900.00, 'World-class noise cancelling, spatial audio, quiet comfort', 'Bose', 4.7, 40, 'images/bose_qc.jpg'),
(129, 'Kindle Paperwhite 16GB', 3, 14999.00, '6.8" display, adjustable warm light, up to 10 weeks battery', 'Amazon', 4.8, 100, 'images/kindle.jpg'),
(130, 'Razer DeathAdder V3 Pro', 1, 14990.00, '63g ultra-lightweight design, 30K optical sensor', 'Razer', 4.9, 50, 'images/razer_mouse.jpg');


-- ============================================================
-- SEED DATA: Default Admin Account
-- Password: admin123 (SHA-256 hashed)
-- ============================================================
INSERT INTO admins (username, email, password_hash, role) VALUES
('admin', 'admin@shopswing.com', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'super_admin');


-- ============================================================
-- INDEXES for performance
-- ============================================================
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_brand ON products(brand);
CREATE INDEX idx_products_price ON products(price);
CREATE INDEX idx_cart_user ON cart(user_id);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_order_items_order ON order_items(order_id);


-- ============================================================
-- Verify seed data
-- ============================================================
SELECT 'Categories:' AS info, COUNT(*) AS count FROM categories
UNION ALL
SELECT 'Products:', COUNT(*) FROM products
UNION ALL
SELECT 'Admins:', COUNT(*) FROM admins;
