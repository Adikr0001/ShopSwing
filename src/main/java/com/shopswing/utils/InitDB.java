package com.shopswing.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Statement;

public class InitDB {

    public static void initTables() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            DatabaseMetaData meta = conn.getMetaData();
            String product = meta.getDatabaseProductName();
            boolean postgres = product != null && product.toLowerCase().contains("postgres");

            if (postgres) {
                System.out.println("Creating PostgreSQL tables...");
                createPostgresTables(stmt);
            } else {
                System.out.println("Creating SQLite tables...");
                createSqliteTables(stmt);
            }

            System.out.println("Tables created successfully!");

            if (!postgres) {
                stmt.execute("PRAGMA journal_mode=WAL");
            }

            try {
                stmt.execute("INSERT INTO admins (username, email, password_hash, role) VALUES " +
                        "('admin', 'admin@shopswing.com', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'super_admin')");
            } catch (Exception ignored) {
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createSqliteTables(Statement stmt) throws Exception {
        stmt.execute("CREATE TABLE IF NOT EXISTS categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL UNIQUE, " +
                "description TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE, " +
                "email TEXT NOT NULL UNIQUE, " +
                "password_hash TEXT NOT NULL, " +
                "full_name TEXT, " +
                "phone TEXT, " +
                "address TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        stmt.execute("CREATE TABLE IF NOT EXISTS admins (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE, " +
                "email TEXT NOT NULL UNIQUE, " +
                "password_hash TEXT NOT NULL, " +
                "role TEXT DEFAULT 'admin', " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        stmt.execute("CREATE TABLE IF NOT EXISTS products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "category_id INTEGER NOT NULL, " +
                "price REAL NOT NULL, " +
                "description TEXT, " +
                "brand TEXT, " +
                "rating REAL DEFAULT 0.0, " +
                "stock INTEGER DEFAULT 0, " +
                "image_url TEXT, " +
                "specifications TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (category_id) REFERENCES categories(id))");

        stmt.execute("CREATE TABLE IF NOT EXISTS cart (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "product_id INTEGER NOT NULL, " +
                "quantity INTEGER NOT NULL DEFAULT 1, " +
                "added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "UNIQUE (user_id, product_id), " +
                "FOREIGN KEY (user_id) REFERENCES users(id), " +
                "FOREIGN KEY (product_id) REFERENCES products(id))");

        stmt.execute("CREATE TABLE IF NOT EXISTS orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "total_amount REAL NOT NULL, " +
                "shipping_address TEXT, " +
                "status TEXT DEFAULT 'Placed', " +
                "payment_method TEXT DEFAULT 'Cash on Delivery', " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(id))");

        stmt.execute("CREATE TABLE IF NOT EXISTS order_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id INTEGER NOT NULL, " +
                "product_id INTEGER NOT NULL, " +
                "product_name TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "quantity INTEGER NOT NULL, " +
                "FOREIGN KEY (order_id) REFERENCES orders(id), " +
                "FOREIGN KEY (product_id) REFERENCES products(id))");
    }

    private static void createPostgresTables(Statement stmt) throws Exception {
        stmt.execute("CREATE TABLE IF NOT EXISTS categories (" +
                "id SERIAL PRIMARY KEY, " +
                "name TEXT NOT NULL UNIQUE, " +
                "description TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                "id SERIAL PRIMARY KEY, " +
                "username TEXT NOT NULL UNIQUE, " +
                "email TEXT NOT NULL UNIQUE, " +
                "password_hash TEXT NOT NULL, " +
                "full_name TEXT, " +
                "phone TEXT, " +
                "address TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        stmt.execute("CREATE TABLE IF NOT EXISTS admins (" +
                "id SERIAL PRIMARY KEY, " +
                "username TEXT NOT NULL UNIQUE, " +
                "email TEXT NOT NULL UNIQUE, " +
                "password_hash TEXT NOT NULL, " +
                "role TEXT DEFAULT 'admin', " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        stmt.execute("CREATE TABLE IF NOT EXISTS products (" +
                "id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "category_id INTEGER NOT NULL REFERENCES categories(id), " +
                "price DOUBLE PRECISION NOT NULL, " +
                "description TEXT, " +
                "brand TEXT, " +
                "rating DOUBLE PRECISION DEFAULT 0.0, " +
                "stock INTEGER DEFAULT 0, " +
                "image_url TEXT, " +
                "specifications TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        stmt.execute("CREATE TABLE IF NOT EXISTS cart (" +
                "id SERIAL PRIMARY KEY, " +
                "user_id INTEGER NOT NULL REFERENCES users(id), " +
                "product_id INTEGER NOT NULL REFERENCES products(id), " +
                "quantity INTEGER NOT NULL DEFAULT 1, " +
                "added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "UNIQUE (user_id, product_id))");

        stmt.execute("CREATE TABLE IF NOT EXISTS orders (" +
                "id SERIAL PRIMARY KEY, " +
                "user_id INTEGER NOT NULL REFERENCES users(id), " +
                "total_amount DOUBLE PRECISION NOT NULL, " +
                "shipping_address TEXT, " +
                "status TEXT DEFAULT 'Placed', " +
                "payment_method TEXT DEFAULT 'Cash on Delivery', " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        stmt.execute("CREATE TABLE IF NOT EXISTS order_items (" +
                "id SERIAL PRIMARY KEY, " +
                "order_id INTEGER NOT NULL REFERENCES orders(id), " +
                "product_id INTEGER NOT NULL REFERENCES products(id), " +
                "product_name TEXT NOT NULL, " +
                "price DOUBLE PRECISION NOT NULL, " +
                "quantity INTEGER NOT NULL)");
    }
}
