# ShopSwing — Online Product Catalog

A complete **Java Servlet + JSP + JDBC + MySQL** e-commerce web application, upgraded from a Java Swing desktop app.

## Tech Stack

| Layer      | Technology                        |
|------------|-----------------------------------|
| Frontend   | JSP, JSTL, HTML5, CSS3, JavaScript |
| Backend    | Java Servlets (MVC Pattern)       |
| Database   | MySQL 8.0 + JDBC                  |
| Server     | Apache Tomcat 9.x                 |
| Build      | Maven                             |

## Prerequisites

- **JDK 8+** (Java SE Development Kit)
- **Apache Tomcat 9.x** ([download](https://tomcat.apache.org/download-90.cgi))
- **MySQL 8.0+** ([download](https://dev.mysql.com/downloads/mysql/))
- **Apache Maven 3.6+** ([download](https://maven.apache.org/download.cgi))
- **IDE**: Eclipse (Enterprise) or IntelliJ IDEA Ultimate (optional)

## Setup Instructions

### 1. Database Setup

Start MySQL and run the schema file:

```bash
mysql -u root -p < sql/shopswing_schema.sql
```

Or open `sql/shopswing_schema.sql` in MySQL Workbench and execute it.

This creates the `shopswing_db` database with 7 tables and seeds 25 products + 6 categories.

### 2. Configure Database Connection

Edit `src/main/java/com/shopswing/utils/DBConnection.java` if your MySQL credentials differ:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/shopswing_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
private static final String DB_USER = "root";        // change if needed
private static final String DB_PASSWORD = "root";     // change if needed
```

### 3. Build with Maven

```bash
cd "online product catalog"
mvn clean package
```

This generates `target/ShopSwing.war`.

### 4. Deploy to Tomcat

**Option A — Manual:**
Copy `target/ShopSwing.war` to Tomcat's `webapps/` folder, then start Tomcat.

**Option B — Eclipse:**
1. Import as "Existing Maven Project"
2. Right-click project → Run As → Run on Server → Select Tomcat 9

**Option C — IntelliJ:**
1. Open folder as project
2. Add Tomcat run configuration
3. Deploy artifact: `ShopSwing:war exploded`

### 5. Access the Application

Open browser: **http://localhost:8080/ShopSwing/**

## Project Structure

```
online product catalog/
├── pom.xml                              # Maven configuration
├── sql/
│   └── shopswing_schema.sql             # MySQL schema + seed data
├── src/main/java/com/shopswing/
│   ├── model/                           # Java beans (POJOs)
│   │   ├── User.java
│   │   ├── Admin.java
│   │   ├── Product.java
│   │   ├── Category.java
│   │   ├── CartItem.java
│   │   ├── Order.java
│   │   └── OrderItem.java
│   ├── dao/                             # Data Access Objects (Phase 2+)
│   ├── servlet/                         # Servlet controllers (Phase 2+)
│   └── utils/
│       └── DBConnection.java            # MySQL connection utility
├── src/main/webapp/
│   ├── WEB-INF/
│   │   └── web.xml                      # Deployment descriptor
│   ├── css/
│   │   └── style.css                    # Dark theme styles
│   ├── js/
│   │   └── main.js                      # Client-side utilities
│   ├── error/
│   │   ├── 404.jsp
│   │   └── 500.jsp
│   └── index.jsp                        # Landing page
├── ProductCatalog.java                  # Original Swing app (preserved)
└── README.md
```

## Database Schema

| Table         | Description                      |
|---------------|----------------------------------|
| `users`       | Registered customers             |
| `admins`      | Admin accounts with roles        |
| `categories`  | Product categories (6 seeded)    |
| `products`    | Product catalog (25 seeded)      |
| `cart`         | User shopping cart items         |
| `orders`      | Placed orders                    |
| `order_items` | Individual items within orders   |

## Default Admin Account

- **Username:** admin
- **Password:** admin123

## Development Phases

- [x] **Phase 1:** Project Setup + Database + Structure
- [ ] **Phase 2:** User Authentication (Login/Register/Logout)
- [ ] **Phase 3:** Product Module (Dynamic catalog, search, filter)
- [ ] **Phase 4:** Cart Module (Add/remove/update cart)
- [ ] **Phase 5:** Checkout + Orders
- [ ] **Phase 6:** Admin Panel
- [ ] **Phase 7:** Final Polish

## License

Educational project — ShopSwing Online Product Catalog.
