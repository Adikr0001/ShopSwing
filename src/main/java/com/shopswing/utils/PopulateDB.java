package com.shopswing.utils;

import com.shopswing.model.Product;
import com.shopswing.dao.ProductDAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PopulateDB {

    public static void main(String[] args) {
        System.out.println("Starting Database Population...");

        try {
            InitDB.initTables();
            ensureCategories();
            populateBaseProducts();
            populatePremiumProducts();
            populateHomeAndGardenProducts();
            populateBeautyProducts();
            populateClothingProducts();
            populateBooksProducts();
            populateSportsProducts();
            populateMoreBeautyProducts();
            populateMoreHomeProducts();
            updateProductImages(); // Update all products with real HD images
            System.out.println("Database Population COMPLETED successfully!");
        } catch (Exception e) {
            System.err.println("Database Population FAILED!");
            e.printStackTrace();
        }
    }

    private static void ensureCategories() throws SQLException {
        String[] categories = {"Electronics", "Clothing", "Books", "Home & Garden", "Sports", "Beauty"};
        String[] descriptions = {
            "Smartphones, laptops, headphones, cameras & gadgets",
            "Men & women fashion, shoes, accessories",
            "Bestsellers, programming, self-help & more",
            "Kitchen appliances, home decor, cleaning",
            "Fitness equipment, supplements, yoga accessories",
            "Skincare, makeup, personal care products"
        };

        try (Connection conn = DBConnection.getConnection()) {
            String checkSql = "SELECT id FROM categories WHERE name = ?";
            String insertSql = "INSERT INTO categories (name, description) VALUES (?, ?)";
            
            for (int i = 0; i < categories.length; i++) {
                PreparedStatement psCheck = conn.prepareStatement(checkSql);
                psCheck.setString(1, categories[i]);
                ResultSet rs = psCheck.executeQuery();
                if (!rs.next()) {
                    PreparedStatement psInsert = conn.prepareStatement(insertSql);
                    psInsert.setString(1, categories[i]);
                    psInsert.setString(2, descriptions[i]);
                    psInsert.executeUpdate();
                    System.out.println("Added category: " + categories[i]);
                }
            }
        }
    }

    private static void populateBaseProducts() throws SQLException {
        // Base products from schema
        Object[][] baseData = {
            {101, "Samsung Galaxy S24 Ultra", 1, 89999.00, "6.8-inch QHD+ AMOLED, 200MP camera, Snapdragon 8 Gen 3", "Samsung", 4.8, 50, "images/samsung_s24.jpg", "Screen: 6.8 inch | RAM: 12GB | Storage: 256GB | Camera: 200MP"},
            {102, "Apple iPhone 15 Pro", 1, 99999.00, "6.1-inch Super Retina XDR, A17 Pro chip, ProRAW camera", "Apple", 4.9, 30, "images/iphone15.jpg", "Chip: A17 Pro | Screen: 6.1 inch | Storage: 128GB | Material: Titanium"},
            {103, "Sony WH-1000XM5", 1, 29990.00, "Industry-leading noise cancellation, 30hr battery", "Sony", 4.7, 80, "images/sony_wh1000.jpg", "Battery: 30 hours | Noise Cancel: Yes | Bluetooth: 5.2 | Weight: 250g"},
            {104, "Dell XPS 15 Laptop", 1, 149999.00, "15.6-inch OLED 3.5K, Intel i9, 32GB RAM, RTX 4070", "Dell", 4.6, 20, "images/dell_xps15.jpg", "CPU: Intel i9 | RAM: 32GB | GPU: RTX 4070 | Screen: OLED 3.5K"},
            {105, "LG 55 inch OLED 4K TV", 1, 89990.00, "55-inch OLED evo, a9 AI Processor, Dolby Vision IQ", "LG", 4.7, 15, "images/lg_oled55.jpg", "Size: 55 inch | Panel: OLED | Resolution: 4K | Refresh: 120Hz"},
            {106, "BoAt Airdopes 141 TWS", 1, 1299.00, "Up to 42 hours total playback, Beast Mode gaming", "boAt", 4.3, 200, "images/boat_airdopes.jpg", "Battery: 42 hours | Water Resist: IPX4 | Bluetooth: 5.1 | Driver: 8mm"},
            {107, "Canon EOS R50 Camera", 1, 67990.00, "24.2MP APS-C sensor, 4K video, dual pixel AF", "Canon", 4.6, 25, "images/canon_r50.jpg", "Sensor: 24.2MP | Video: 4K | AF: Dual Pixel | Connectivity: Wi-Fi"},
            {108, "Apple iPad Air M2", 1, 59900.00, "11-inch Liquid Retina, M2 chip, USB-C connector", "Apple", 4.8, 40, "images/ipad_air.jpg", "Chip: M2 | Screen: 11 inch | Storage: 64GB | Weight: 461g"},
            {111, "Levis 501 Original Jeans", 2, 3999.00, "Classic straight fit, 100% cotton denim", "Levi's", 4.4, 100, "images/levis_501.jpg", "Material: 100% Cotton | Fit: Straight | Closure: Button Fly | Wash: Machine"},
            {112, "Nike Air Max 270", 2, 9995.00, "Max Air unit, breathable mesh upper, all-day comfort", "Nike", 4.5, 75, "images/nike_airmax.jpg", "Sole: Max Air | Upper: Mesh | Weight: 280g | Origin: Vietnam"},
            {115, "Atomic Habits", 3, 499.00, "Transform your life with tiny changes - James Clear", "James Clear", 4.9, 500, "images/atomic_habits.jpg", "Author: James Clear | Genre: Self-Help | Pages: 320 | Language: English"},
            {116, "Clean Code", 3, 1299.00, "A handbook of agile software craftsmanship, R.C.Martin", "Robert C Martin", 4.8, 300, "images/clean_code.jpg", "Author: R.C. Martin | Genre: Tech | Pages: 464 | Language: English"},
            {119, "Dyson V15 Detect", 4, 52900.00, "Laser dust detection, 60 min run time, HEPA filter", "Dyson", 4.7, 18, "images/dyson_v15.jpg", "Run Time: 60 min | Filter: HEPA | Sensor: Laser | Power: 240AW"},
            {126, "Sony PlayStation 5 Slim", 1, 44990.00, "Ultra-high speed SSD, integrated I/O, Ray Tracing", "Sony", 4.9, 15, "images/ps5.jpg", "Storage: 1TB SSD | CPU: Custom RDNA 2 | Ray Tracing: Yes | Resolution: 4K"},
            {127, "Nintendo Switch OLED", 1, 32990.00, "7-inch OLED screen, wide adjustable stand, wired LAN port", "Nintendo", 4.8, 20, "images/switch_oled.jpg", "Screen: 7 inch OLED | Storage: 64GB | Dock: Wired LAN | Controllers: Joy-Con"}
        };

        for (Object[] data : baseData) {
            insertIfNotExists(data);
        }
    }

    private static void populatePremiumProducts() throws SQLException {
        Object[][] premiumData = {
            {201, "MacBook Pro M3 Max", 1, 349999.00, "16-inch Liquid Retina XDR, M3 Max chip with 16-core CPU and 40-core GPU, 64GB RAM, 2TB SSD.", "Apple", 5.0, 10, "images/macbook_m3.jpg", "Chip: M3 Max | CPU: 16-core | GPU: 40-core | RAM: 64GB | SSD: 2TB"},
            {202, "Sony A7R V Camera", 1, 389999.00, "61.0MP full-frame sensor, 8K video, AI-based autofocus, 8-stop image stabilization.", "Sony", 4.9, 5, "images/sony_a7rv.jpg", "Sensor: 61MP Full | Video: 8K | IBIS: 8-stop | Processor: BIONZ XR"},
            {203, "Rolex Submariner Date", 2, 1250000.00, "Oyster, 41 mm, Oystersteel and yellow gold, Cerachrom bezel in black ceramic.", "Rolex", 4.9, 2, "images/rolex_sub.jpg", "Material: 18ct Gold | Size: 41mm | Movement: Calibre 3235 | Waterproof: 300m"},
            {204, "Herman Miller Chair", 4, 185000.00, "The highest standard for pressure distribution, natural alignment, and support for healthy movement.", "Herman Miller", 4.8, 15, "images/herman_miller.jpg", "Warranty: 12 Years | Design: Ergonomic | Fabric: Pixelated | Adjust: 4D"},
            {205, "Sennheiser HE-1", 1, 4500000.00, "The world's best headphones. Electrostatic system with marble amplifier and gold-vaporized ceramic transducers.", "Sennheiser", 5.0, 1, "images/sennheiser_he1.jpg", "System: Electrostatic | Amp: Valve | Material: Carrara Marble | Frequency: 8Hz - 100kHz"},
            {206, "Air Jordan 1 Retro", 2, 18500.00, "The sneaker that started it all. Premium leather and iconic Chicago colorway.", "Nike", 4.8, 25, "images/jordan1.jpg", "Color: Chicago | Material: Leather | Type: High Top | Launch: 1985"},
            {207, "Dior Sauvage Elixir", 6, 14500.00, "An extraordinarily concentrated fragrance steeped in the emblematic freshness of Sauvage.", "Dior", 4.7, 50, "images/dior_sauvage.jpg", "Type: Elixir | Size: 60ml | Notes: Lavender, Licorice | Longevity: 12h+"},
            {208, "Razer Blade 16", 1, 359999.00, "World's First OLED 240Hz Display, Intel Core i9-14900HX, NVIDIA GeForce RTX 4090.", "Razer", 4.9, 8, "images/razer_blade.jpg", "Screen: OLED 240Hz | CPU: i9-14900HX | GPU: RTX 4090 | RAM: 32GB"}
        };

        for (Object[] data : premiumData) {
            insertIfNotExists(data);
        }
    }

    private static void populateHomeAndGardenProducts() throws SQLException {
        Object[][] homeData = {
            {301, "Philips Air Fryer XXL", 4, 14999.00, "Rapid Air Technology, 1.4kg capacity, digital display, 7 presets for easy cooking.", "Philips", 4.6, 45, "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=400", "Capacity: 1.4kg | Power: 2225W | Presets: 7 | Technology: Rapid Air"},
            {302, "IKEA KALLAX Shelf Unit", 4, 5999.00, "Versatile storage unit, 4x4 compartments, white finish, easy assembly.", "IKEA", 4.5, 60, "https://images.unsplash.com/photo-1595428774223-ef52624120d2?w=400", "Size: 147x147cm | Compartments: 16 | Material: Particleboard | Color: White"},
            {303, "Instant Pot Duo 7-in-1", 4, 8999.00, "Electric pressure cooker, slow cooker, rice cooker, steamer, saute pan, yogurt maker.", "Instant Pot", 4.8, 80, "https://images.unsplash.com/photo-1585515320310-259814833e62?w=400", "Capacity: 6 Qt | Functions: 7 | Power: 1000W | Material: Stainless Steel"},
            {304, "Bosch Washing Machine 8kg", 4, 42990.00, "Front load, EcoSilence Drive, AntiVibration design, 15 wash programs.", "Bosch", 4.7, 20, "https://images.unsplash.com/photo-1626806787461-102c1bfaaea1?w=400", "Capacity: 8kg | RPM: 1400 | Programs: 15 | Energy: A+++"},
            {305, "KitchenAid Stand Mixer", 4, 34999.00, "Iconic tilt-head design, 10 speeds, 4.8L stainless steel bowl, includes 3 attachments.", "KitchenAid", 4.9, 25, "https://images.unsplash.com/photo-1594385208974-2e75f8d7bb48?w=400", "Capacity: 4.8L | Speeds: 10 | Power: 300W | Attachments: 3"},
            {306, "Xiaomi Robot Vacuum S10+", 4, 29999.00, "LiDAR navigation, 4000Pa suction, self-emptying dock, app control.", "Xiaomi", 4.6, 35, "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400", "Suction: 4000Pa | Runtime: 130min | Navigation: LiDAR | Dustbin: 400ml"},
            {307, "Urban Company Air Purifier", 4, 12999.00, "HEPA H13 filter, covers 500 sq ft, real-time AQI display, sleep mode.", "Urban Company", 4.4, 55, "https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=400", "Coverage: 500 sqft | Filter: HEPA H13 | CADR: 350 m3/h | Noise: 25dB"},
            {308, "Prestige Induction Cooktop", 4, 2499.00, "1600W power, push button control, automatic voltage regulator, Indian menu.", "Prestige", 4.3, 100, "https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400", "Power: 1600W | Voltage: 230V | Timer: Yes | Indian Menu: Yes"},
            {309, "Bajaj Water Heater 15L", 4, 7999.00, "Glass-lined tank, titanium armour, 5-star energy rating, child safety mode.", "Bajaj", 4.5, 40, "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=400", "Capacity: 15L | Rating: 5 Star | Tank: Glass Lined | Warranty: 5 Years"},
            {310, "Havells Ceiling Fan 1200mm", 4, 2199.00, "Aerodynamic blades, high air delivery, dust resistant, energy efficient.", "Havells", 4.4, 150, "https://images.unsplash.com/photo-1509644851169-2acc08aa25b5?w=400", "Sweep: 1200mm | Speed: 380 RPM | Power: 72W | Air Delivery: 230 CMM"}
        };

        for (Object[] data : homeData) {
            insertIfNotExists(data);
        }
    }

    private static void populateBeautyProducts() throws SQLException {
        Object[][] beautyData = {
            {401, "Maybelline Fit Me Foundation", 6, 499.00, "Matte + Poreless liquid foundation, oil-free, natural finish for normal to oily skin.", "Maybelline", 4.4, 200, "https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=400", "Type: Liquid | Finish: Matte | SPF: 18 | Coverage: Medium"},
            {402, "MAC Ruby Woo Lipstick", 6, 1950.00, "Iconic retro matte red lipstick, long-wearing, highly pigmented.", "MAC", 4.8, 75, "https://images.unsplash.com/photo-1586495777744-4413f21062fa?w=400", "Finish: Retro Matte | Color: Ruby Woo | Weight: 3g | Lasting: 8 Hours"},
            {403, "The Ordinary Niacinamide 10%", 6, 690.00, "High-strength vitamin and mineral blemish formula, reduces pores and oil.", "The Ordinary", 4.6, 120, "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=400", "Niacinamide: 10% | Zinc: 1% | Size: 30ml | Skin Type: All"},
            {404, "Lakme Eyeconic Kajal", 6, 225.00, "Smudge-proof, waterproof, 22-hour stay, deep black color.", "Lakme", 4.5, 300, "https://images.unsplash.com/photo-1512496015851-a90fb38ba796?w=400", "Type: Kajal | Stay: 22 Hours | Waterproof: Yes | Weight: 0.35g"},
            {405, "Forest Essentials Night Cream", 6, 2175.00, "Ayurvedic night treatment, sandalwood and saffron, intensive repair.", "Forest Essentials", 4.7, 40, "https://images.unsplash.com/photo-1570194065650-d99fb4b38b15?w=400", "Type: Night Cream | Size: 50g | Key: Sandalwood | Skin: All Types"},
            {406, "Biotique Bio Aloe Vera Gel", 6, 199.00, "Pure aloe vera gel for face and body, hydrating and soothing.", "Biotique", 4.3, 250, "https://images.unsplash.com/photo-1608248597279-f99d160bfcbc?w=400", "Size: 120ml | Type: Gel | Ingredient: Aloe Vera | Use: Face & Body"},
            {407, "Nivea Soft Moisturizing Cream", 6, 299.00, "Light moisturizing cream with vitamin E and jojoba oil, non-greasy.", "Nivea", 4.5, 180, "https://images.unsplash.com/photo-1611930022073-b7a4ba5fcccd?w=400", "Size: 200ml | Type: Cream | Vitamin: E | Texture: Light"},
            {408, "Dove Hair Fall Rescue Shampoo", 6, 399.00, "Nutritive solutions, reduces hair fall, strengthens from root to tip.", "Dove", 4.4, 160, "https://images.unsplash.com/photo-1535585209827-a15fcdbc4c2d?w=400", "Size: 650ml | Type: Shampoo | Concern: Hair Fall | Formula: Nutritive"},
            {409, "Plum Green Tea Face Wash", 6, 345.00, "Gentle face wash for oily skin, glycolic acid, natural green tea extracts.", "Plum", 4.6, 140, "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=400", "Size: 100ml | Skin: Oily/Acne | Key: Green Tea | Type: Gel"},
            {410, "Himalaya Neem Face Pack", 6, 135.00, "Purifying neem pack, removes impurities, controls excess oil.", "Himalaya", 4.3, 220, "https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?w=400", "Size: 100ml | Type: Face Pack | Key: Neem | Skin: Oily/Combination"}
        };

        for (Object[] data : beautyData) {
            insertIfNotExists(data);
        }
    }

    private static void populateClothingProducts() throws SQLException {
        Object[][] clothingData = {
            {501, "Allen Solly Formal Shirt", 2, 1499.00, "Slim fit formal shirt, 100% cotton, wrinkle-resistant fabric.", "Allen Solly", 4.4, 120, "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=400", "Material: Cotton | Fit: Slim | Collar: Regular | Wash: Machine"},
            {502, "Adidas Ultraboost 22", 2, 16999.00, "Responsive Boost midsole, Primeknit upper, Continental rubber outsole.", "Adidas", 4.7, 50, "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400", "Sole: Boost | Upper: Primeknit | Weight: 310g | Drop: 10mm"},
            {503, "H&M Oversized Hoodie", 2, 1999.00, "Relaxed fit hoodie, soft cotton blend, kangaroo pocket.", "H&M", 4.3, 80, "https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=400", "Material: Cotton Blend | Fit: Oversized | Hood: Drawstring | Pocket: Kangaroo"},
            {504, "Zara Leather Jacket", 2, 7990.00, "Genuine leather biker jacket, asymmetric zip, quilted shoulders.", "Zara", 4.6, 30, "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=400", "Material: Leather | Style: Biker | Zip: Asymmetric | Lining: Polyester"},
            {505, "Van Heusen Chinos", 2, 1899.00, "Regular fit chinos, stretch cotton, wrinkle-free finish.", "Van Heusen", 4.4, 100, "https://images.unsplash.com/photo-1473966968600-fa801b869a1a?w=400", "Material: Cotton Stretch | Fit: Regular | Closure: Zip | Pockets: 4"},
            {506, "Puma RS-X Sneakers", 2, 8999.00, "Retro-inspired design, RS cushioning technology, mesh upper.", "Puma", 4.5, 65, "https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=400", "Sole: RS | Upper: Mesh | Style: Retro | Weight: 340g"},
            {507, "FabIndia Kurti Set", 2, 2499.00, "Hand-block printed kurti with palazzo, pure cotton fabric.", "FabIndia", 4.6, 90, "https://images.unsplash.com/photo-1583391733956-6c78276477e2?w=400", "Material: Cotton | Print: Block | Set: 2 Piece | Wash: Hand"},
            {508, "Tommy Hilfiger Polo", 2, 3499.00, "Classic fit polo shirt, signature flag logo, pique cotton.", "Tommy Hilfiger", 4.5, 70, "https://images.unsplash.com/photo-1625910513413-5fc42c511e36?w=400", "Material: Pique Cotton | Fit: Classic | Collar: Ribbed | Logo: Chest"},
            {509, "Woodland Leather Boots", 2, 4995.00, "Genuine leather boots, anti-skid sole, water-resistant.", "Woodland", 4.6, 45, "https://images.unsplash.com/photo-1542840410-8e894e1d4b75?w=400", "Material: Leather | Sole: Rubber | Water: Resistant | Closure: Lace"},
            {510, "US Polo Assn Jacket", 2, 3999.00, "Lightweight bomber jacket, water-repellent, ribbed cuffs.", "US Polo", 4.4, 55, "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=400", "Material: Polyester | Style: Bomber | Water: Repellent | Pockets: 3"},
            {511, "Biba Anarkali Dress", 2, 3299.00, "Ethnic anarkali suit, embroidered yoke, flared silhouette.", "Biba", 4.5, 60, "https://images.unsplash.com/photo-1583391733981-8b530c46a7d5?w=400", "Material: Rayon | Style: Anarkali | Work: Embroidery | Length: Ankle"},
            {512, "Raymond Blazer", 2, 8999.00, "Single-breasted blazer, wool blend, notch lapel.", "Raymond", 4.7, 25, "https://images.unsplash.com/photo-1507679799987-c73779587ccf?w=400", "Material: Wool Blend | Fit: Regular | Buttons: 2 | Lapel: Notch"},
            {513, "Reebok Classic Leather", 2, 6999.00, "Iconic leather sneaker, soft leather upper, EVA midsole.", "Reebok", 4.5, 80, "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=400", "Material: Leather | Sole: Rubber | Midsole: EVA | Style: Classic"}
        };
        for (Object[] data : clothingData) {
            insertIfNotExists(data);
        }
    }

    private static void populateBooksProducts() throws SQLException {
        Object[][] booksData = {
            {601, "The Psychology of Money", 3, 399.00, "Timeless lessons on wealth, greed, and happiness by Morgan Housel.", "Morgan Housel", 4.8, 400, "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400", "Author: Morgan Housel | Genre: Finance | Pages: 256 | Language: English"},
            {602, "Ikigai", 3, 350.00, "The Japanese secret to a long and happy life.", "Hector Garcia", 4.7, 350, "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400", "Author: Hector Garcia | Genre: Self-Help | Pages: 208 | Language: English"},
            {603, "Rich Dad Poor Dad", 3, 399.00, "What the rich teach their kids about money that the poor and middle class do not.", "Robert Kiyosaki", 4.6, 450, "https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=400", "Author: Robert Kiyosaki | Genre: Finance | Pages: 336 | Language: English"},
            {604, "The Alchemist", 3, 299.00, "A magical story about following your dreams by Paulo Coelho.", "Paulo Coelho", 4.8, 500, "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=400", "Author: Paulo Coelho | Genre: Fiction | Pages: 208 | Language: English"},
            {605, "Sapiens", 3, 599.00, "A brief history of humankind by Yuval Noah Harari.", "Yuval Harari", 4.9, 300, "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=400", "Author: Yuval Harari | Genre: History | Pages: 512 | Language: English"},
            {606, "The Subtle Art of Not Giving a F*ck", 3, 399.00, "A counterintuitive approach to living a good life.", "Mark Manson", 4.5, 380, "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400", "Author: Mark Manson | Genre: Self-Help | Pages: 224 | Language: English"},
            {607, "Deep Work", 3, 499.00, "Rules for focused success in a distracted world by Cal Newport.", "Cal Newport", 4.7, 280, "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400", "Author: Cal Newport | Genre: Productivity | Pages: 304 | Language: English"},
            {608, "Think and Grow Rich", 3, 299.00, "The landmark bestseller on success principles by Napoleon Hill.", "Napoleon Hill", 4.6, 420, "https://images.unsplash.com/photo-1491841573634-28140fc7ced7?w=400", "Author: Napoleon Hill | Genre: Self-Help | Pages: 320 | Language: English"},
            {609, "The Pragmatic Programmer", 3, 2499.00, "Your journey to mastery, 20th Anniversary Edition.", "David Thomas", 4.9, 150, "https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400", "Author: David Thomas | Genre: Programming | Pages: 352 | Language: English"},
            {610, "You Don't Know JS", 3, 1899.00, "Deep dive into the core mechanisms of JavaScript.", "Kyle Simpson", 4.7, 180, "https://images.unsplash.com/photo-1589998059171-988d887df646?w=400", "Author: Kyle Simpson | Genre: Programming | Pages: 278 | Language: English"},
            {611, "Design Patterns", 3, 3999.00, "Elements of reusable object-oriented software by Gang of Four.", "Erich Gamma", 4.8, 100, "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400", "Author: Gang of Four | Genre: Programming | Pages: 416 | Language: English"},
            {612, "The Monk Who Sold His Ferrari", 3, 299.00, "A fable about fulfilling your dreams by Robin Sharma.", "Robin Sharma", 4.5, 360, "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400", "Author: Robin Sharma | Genre: Self-Help | Pages: 224 | Language: English"},
            {613, "Zero to One", 3, 499.00, "Notes on startups, or how to build the future by Peter Thiel.", "Peter Thiel", 4.6, 250, "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400", "Author: Peter Thiel | Genre: Business | Pages: 224 | Language: English"}
        };
        for (Object[] data : booksData) {
            insertIfNotExists(data);
        }
    }

    private static void populateSportsProducts() throws SQLException {
        Object[][] sportsData = {
            {701, "Yonex Badminton Racket", 5, 2999.00, "Astrox 88D Pro, high tension frame, rotational generator system.", "Yonex", 4.7, 60, "https://images.unsplash.com/photo-1617883861744-13b534e3b928?w=400", "Weight: 83g | Balance: Head Heavy | Tension: 28lbs | Material: Graphite"},
            {702, "Nivia Football", 5, 899.00, "FIFA Quality Pro certified, 32 panel design, hand-stitched.", "Nivia", 4.5, 150, "https://images.unsplash.com/photo-1614632537423-1e6c2e7e0aab?w=400", "Size: 5 | Panels: 32 | Material: PU | FIFA: Certified"},
            {703, "Decathlon Yoga Mat", 5, 799.00, "6mm thickness, anti-slip surface, lightweight and portable.", "Decathlon", 4.4, 200, "https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=400", "Thickness: 6mm | Material: TPE | Size: 183x61cm | Weight: 1.2kg"},
            {704, "Cosco Cricket Bat", 5, 1499.00, "English willow bat, short handle, full size for adults.", "Cosco", 4.3, 80, "https://images.unsplash.com/photo-1531415074968-036ba1b575da?w=400", "Wood: English Willow | Handle: Short | Size: Full | Weight: 1.2kg"},
            {705, "MuscleBlaze Whey Protein", 5, 2499.00, "25g protein per serving, low carb, fast absorbing whey isolate.", "MuscleBlaze", 4.6, 120, "https://images.unsplash.com/photo-1593095948071-474c5cc2989d?w=400", "Protein: 25g | Servings: 30 | Flavor: Chocolate | Type: Isolate"},
            {706, "Adidas Tennis Ball Pack", 5, 599.00, "Championship grade tennis balls, pressurized for consistent bounce.", "Adidas", 4.4, 180, "https://images.unsplash.com/photo-1554068865-24cecd4e34b8?w=400", "Quantity: 3 | Type: Pressurized | Grade: Championship | Core: Rubber"},
            {707, "Fitbit Charge 5", 5, 14999.00, "Advanced fitness tracker, GPS, heart rate, stress management.", "Fitbit", 4.7, 40, "https://images.unsplash.com/photo-1575311373937-040b8e1fd5b6?w=400", "Display: AMOLED | GPS: Yes | Battery: 7 days | Water: 50m"},
            {708, "Kettlebell 16kg", 5, 1899.00, "Cast iron kettlebell, vinyl coated, wide grip handle.", "Decathlon", 4.5, 70, "https://images.unsplash.com/photo-1517963879433-6ad2b056d712?w=400", "Weight: 16kg | Material: Cast Iron | Coating: Vinyl | Handle: Wide"},
            {709, "Resistance Bands Set", 5, 699.00, "5 different resistance levels, latex-free, with door anchor.", "Boldfit", 4.4, 250, "https://images.unsplash.com/photo-1598289431512-b97b0917affc?w=400", "Bands: 5 | Levels: Light to X-Heavy | Material: TPE | Accessories: Yes"},
            {710, "Spalding Basketball", 5, 1999.00, "Official NBA size 7, composite leather, deep channel design.", "Spalding", 4.6, 90, "https://images.unsplash.com/photo-1546519638-68e109498ffc?w=400", "Size: 7 | Material: Composite | Circumference: 75cm | NBA: Official"},
            {711, "Swimming Goggles", 5, 499.00, "Anti-fog lens, UV protection, adjustable silicone strap.", "Speedo", 4.5, 160, "https://images.unsplash.com/photo-1519315901367-f34ff9154487?w=400", "Lens: Anti-Fog | UV: Protection | Strap: Silicone | Fit: Universal"},
            {712, "Adjustable Dumbbells 24kg", 5, 8999.00, "Quick-change weight system, 2.5kg to 24kg per dumbbell.", "Bowflex", 4.8, 30, "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=400", "Range: 2.5-24kg | Increments: 2.5kg | Plates: 15 | Material: Steel"},
            {713, "Treadmill T500", 5, 34999.00, "2.5HP motor, 12km/h max speed, foldable design, LCD display.", "PowerMax", 4.6, 20, "https://images.unsplash.com/photo-1576678927484-cc907957088c?w=400", "Motor: 2.5HP | Speed: 12km/h | Incline: Manual | Display: LCD"},
            {714, "Boxing Gloves 12oz", 5, 1299.00, "Premium synthetic leather, multi-layer foam padding.", "Everlast", 4.5, 100, "https://images.unsplash.com/photo-1549719386-74dfcbf7dbed?w=400", "Weight: 12oz | Material: Synthetic | Padding: Multi-Layer | Closure: Velcro"},
            {715, "Foam Roller 18 inch", 5, 599.00, "High-density foam, textured surface for deep tissue massage.", "TriggerPoint", 4.6, 140, "https://images.unsplash.com/photo-1518611012118-696072aa579a?w=400", "Length: 18 inch | Density: High | Surface: Textured | Use: Recovery"}
        };
        for (Object[] data : sportsData) {
            insertIfNotExists(data);
        }
    }

    private static void populateMoreBeautyProducts() throws SQLException {
        Object[][] moreBeautyData = {
            {411, "L'Oreal Paris Serum", 6, 799.00, "Revitalift Hyaluronic Acid serum, intense hydration, plumping effect.", "L'Oreal", 4.6, 130, "https://images.unsplash.com/photo-1617897903246-719242758050?w=400", "Size: 30ml | Key: Hyaluronic Acid | Type: Serum | Skin: All"},
            {412, "Mamaearth Vitamin C Face Wash", 6, 349.00, "Natural face wash with vitamin C and turmeric for glowing skin.", "Mamaearth", 4.4, 200, "https://images.unsplash.com/photo-1556228578-0d85b1a4d571?w=400", "Size: 100ml | Key: Vitamin C | Type: Face Wash | Natural: Yes"},
            {413, "Colorbar Lipstick Set", 6, 1299.00, "Velvet matte lipstick trio, long-lasting, highly pigmented.", "Colorbar", 4.5, 80, "https://images.unsplash.com/photo-1586495777744-4413f21062fa?w=400", "Quantity: 3 | Finish: Velvet Matte | Lasting: 8 Hours | Pigment: High"},
            {414, "WOW Skin Science Hair Oil", 6, 449.00, "Onion black seed hair oil, promotes hair growth, reduces hair fall.", "WOW", 4.5, 170, "https://images.unsplash.com/photo-1527799820374-dcf8d9d4a388?w=400", "Size: 200ml | Key: Onion | Type: Hair Oil | Concern: Hair Fall"},
            {415, "Kama Ayurveda Rose Water", 6, 495.00, "Pure rose water toner, hydrating and refreshing, natural.", "Kama Ayurveda", 4.7, 110, "https://images.unsplash.com/photo-1608248597279-f99d160bfcbc?w=400", "Size: 200ml | Type: Toner | Ingredient: Rose | Use: Face"}
        };
        for (Object[] data : moreBeautyData) {
            insertIfNotExists(data);
        }
    }

    private static void populateMoreHomeProducts() throws SQLException {
        Object[][] moreHomeData = {
            {311, "LG Microwave Oven 28L", 4, 12990.00, "Convection microwave, 28L capacity, 251 auto cook menu.", "LG", 4.6, 35, "https://images.unsplash.com/photo-1574269909862-7e1d70bb8078?w=400", "Capacity: 28L | Type: Convection | Menus: 251 | Power: 900W"},
            {312, "Philips Juicer Mixer", 4, 4999.00, "750W motor, 3 jars, leak-proof lid, turbo function.", "Philips", 4.5, 60, "https://images.unsplash.com/photo-1570222094114-d054a817e56b?w=400", "Power: 750W | Jars: 3 | Speed: 3 | Warranty: 2 Years"},
            {313, "Samsung Refrigerator 253L", 4, 24990.00, "Frost free double door, digital inverter, convertible modes.", "Samsung", 4.7, 25, "https://images.unsplash.com/photo-1571175443880-49e1d25b2bc5?w=400", "Capacity: 253L | Type: Frost Free | Inverter: Yes | Star: 3"},
            {314, "Crompton Geyser 25L", 4, 8499.00, "Storage water heater, anti-rust tank, 5-star energy rating.", "Crompton", 4.4, 50, "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=400", "Capacity: 25L | Rating: 5 Star | Tank: Anti-Rust | Warranty: 7 Years"},
            {315, "Symphony Air Cooler", 4, 9999.00, "Personal air cooler, 31L tank, honeycomb pad, i-Pure technology.", "Symphony", 4.3, 45, "https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=400", "Tank: 31L | Type: Personal | Pad: Honeycomb | Coverage: 400 sqft"}
        };
        for (Object[] data : moreHomeData) {
            insertIfNotExists(data);
        }
    }

    private static void updateProductImages() throws SQLException {
        // Real HD product images from various sources
        Object[][] imageUpdates = {
            // Electronics
            {101, "images/samsung_s24.jpg"},
            {102, "images/products/product_102.jpg"},
            {103, "images/sony_wh1000.jpg"},
            {104, "images/dell_xps15.jpg"},
            {105, "images/lg_oled55.jpg"},
            {106, "images/boat_airdopes.jpg"},
            {107, "images/canon_r50.jpg"},
            {108, "images/products/product_108.jpg"},
            {126, "images/ps5.jpg"},
            {127, "images/switch_oled.jpg"},
            {201, "images/macbook_m3.jpg"},
            {202, "images/sony_a7rv.jpg"},
            {205, "images/sennheiser_he1.jpg"},
            {208, "images/razer_blade.jpg"},
            
            // Clothing
            {111, "images/products/product_111.jpg"},
            {112, "images/products/product_112.png"},
            {203, "images/rolex_sub.jpg"},
            {206, "images/products/product_206.png"},
            {501, "images/products/product_501.jpg"},
            {502, "images/products/product_502.webp"},
            {503, "images/products/product_503.jpg"},
            {504, "images/products/product_504.jpg"},
            {505, "images/products/product_505.jpg"},
            {506, "images/products/product_506.jpg"},
            {507, "images/products/product_507.jpg"},
            {508, "images/products/product_508.jpg"},
            {509, "images/products/product_509.png"},
            {510, "images/products/product_510.jpg"},
            {511, "images/products/product_511.jpg"},
            {512, "images/products/product_512.jpg"},
            {513, "images/products/product_513.jpg"},
            
            // Books - Using actual book cover images
            {115, "images/products/product_115.jpg"},
            {116, "images/products/product_116.jpg"},
            {601, "images/products/product_601.jpg"},
            {602, "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1503904821i/36072670.jpg"},
            {603, "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1583083368i/69571.jpg"},
            {604, "images/products/product_604.jpg"},
            {605, "images/products/product_605.jpg"},
            {606, "images/products/product_606.jpg"},
            {607, "images/products/product_607.jpg"},
            {608, "images/products/product_608.jpg"},
            {609, "images/products/product_609.jpg"},
            {610, "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1574879047i/22221110.jpg"},
            {611, "images/products/product_611.jpg"},
            {612, "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1604562527i/43877.jpg"},
            {613, "images/products/product_613.jpg"},
            
            // Home & Garden
            {119, "images/dyson_v15.jpg"},
            {204, "images/herman_miller.jpg"},
            {301, "images/products/product_301.webp"},
            {302, "images/products/product_302.jpg"},
            {303, "images/products/product_303.jpg"},
            {304, "images/products/product_304.png"},
            {305, "images/products/product_305.jpg"},
            {306, "images/products/product_306.png"},
            {307, "images/products/product_307.jpg"},
            {308, "images/products/product_308.jpg"},
            {309, "images/products/product_309.jpg"},
            {310, "images/products/product_310.jpg"},
            {311, "images/products/product_311.jpg"},
            {312, "images/products/product_312.jpg"},
            {313, "images/products/product_313.jpg"},
            {314, "images/products/product_314.jpg"},
            {315, "images/products/product_315.png"},
            
            // Sports
            {701, "images/products/product_701.png"},
            {702, "images/products/product_702.jpg"},
            {703, "images/products/product_703.jpg"},
            {704, "images/products/product_704.jpg"},
            {705, "images/products/product_705.jpg"},
            {706, "images/products/product_706.jpg"},
            {707, "images/products/product_707.png"},
            {708, "images/products/product_708.png"},
            {709, "images/products/product_709.webp"},
            {710, "images/products/product_710.jpg"},
            {711, "images/products/product_711.jpg"},
            {712, "images/products/product_712.jpg"},
            {713, "images/products/product_713.jpg"},
            {714, "images/products/product_714.jpg"},
            {715, "images/products/product_715.jpg"},
            
            // Beauty
            {207, "images/dior_sauvage.jpg"},
            {401, "images/products/product_401.jpg"},
            {402, "images/products/product_402.jpg"},
            {403, "images/products/product_403.png"},
            {404, "images/products/product_404.jpg"},
            {405, "images/products/product_405.jpg"},
            {406, "images/products/product_406.jpg"},
            {407, "images/products/product_407.jpg"},
            {408, "images/products/product_408.jpg"},
            {409, "images/products/product_409.jpg"},
            {410, "images/products/product_410.jpg"},
            {411, "images/products/product_411.png"},
            {412, "images/products/product_412.png"},
            {413, "images/products/product_413.jpg"},
            {414, "images/products/product_414.jpg"},
            {415, "images/products/product_415.jpg"}
        };
        
        try (Connection conn = DBConnection.getConnection()) {
            String updateSql = "UPDATE products SET image_url = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(updateSql);
            
            for (Object[] update : imageUpdates) {
                ps.setString(1, (String) update[1]);
                ps.setInt(2, (Integer) update[0]);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("Updated image for product ID: " + update[0]);
                }
            }
        }
        System.out.println("Product images updated with real HD images!");
    }

    private static void insertIfNotExists(Object[] data) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String checkSql = "SELECT id FROM products WHERE name = ?";
            PreparedStatement psCheck = conn.prepareStatement(checkSql);
            psCheck.setString(1, (String) data[1]);
            ResultSet rs = psCheck.executeQuery();
            if (!rs.next()) {
                String insertSql = "INSERT INTO products (id, name, category_id, price, description, brand, rating, stock, image_url, specifications) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement psInsert = conn.prepareStatement(insertSql);
                psInsert.setInt(1, (Integer) data[0]);
                psInsert.setString(2, (String) data[1]);
                psInsert.setInt(3, (Integer) data[2]);
                psInsert.setDouble(4, (Double) data[3]);
                psInsert.setString(5, (String) data[4]);
                psInsert.setString(6, (String) data[5]);
                psInsert.setDouble(7, (Double) data[6]);
                psInsert.setInt(8, (Integer) data[7]);
                psInsert.setString(9, (String) data[8]);
                psInsert.setString(10, (String) data[9]);
                psInsert.executeUpdate();
                System.out.println("Inserted product: " + data[1]);
            } else {
                System.out.println("Product already exists: " + data[1]);
            }
        }
    }
}
