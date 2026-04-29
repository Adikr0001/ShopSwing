<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="ShopSwing - Premium Online Product Catalog. Browse electronics, clothing, books and more.">
    <title>ShopSwing — Online Product Catalog</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <!-- Navigation Bar -->
    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/" class="navbar-brand">&#9889; ShopSwing</a>
        <div class="nav-links">
            <c:choose>
                <c:when test="${not empty sessionScope.user}">
                    <a href="${pageContext.request.contextPath}/products">Products</a>
                    <span class="nav-separator">|</span>
                    <a href="${pageContext.request.contextPath}/cart" style="color: #FBBF24;">Cart</a>
                    <span class="nav-separator">|</span>
                    <a href="${pageContext.request.contextPath}/orders" style="color: #34D399;">Orders</a>
                    <span class="nav-separator">|</span>
                    <a href="#" style="color: #B4D4FF;">Hi, ${sessionScope.user.username}</a>
                    <span class="nav-separator">|</span>
                    <a href="${pageContext.request.contextPath}/logout" style="color: #B46464;">Logout</a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-primary btn-sm">Sign In</a>
                    <a href="${pageContext.request.contextPath}/register" class="btn btn-outline btn-sm">Register</a>
                </c:otherwise>
            </c:choose>
        </div>
    </nav>

    <!-- Hero Section -->
    <div style="text-align:center; padding: 5rem 2rem 3rem; background: linear-gradient(135deg, #0A0E18 0%, #1A1040 50%, #0A0E18 100%);">
        <h1 style="font-size: 2.8rem; color: #fff; margin-bottom: 0.5rem;" class="fade-in">
            &#9889; Shop<span style="color: #4F8CFF;">Swing</span>
        </h1>
        <p style="color: #8291B9; font-size: 1.15rem; max-width: 600px; margin: 0 auto 2rem;" class="fade-in">
            Premium Online Product Catalog — Electronics, Fashion, Books & More
        </p>

        <div style="display: flex; gap: 1rem; justify-content: center; flex-wrap: wrap;" class="fade-in">
            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary btn-lg">
                Browse Products
            </a>
            <c:if test="${empty sessionScope.user}">
                <a href="${pageContext.request.contextPath}/register" class="btn btn-outline btn-lg">
                    Create Account
                </a>
            </c:if>
        </div>
    </div>

    <!-- Categories Section -->
    <div class="container" style="padding: 3rem 1.5rem;">
        <h2 style="text-align: center; margin-bottom: 2rem;">Shop by Category</h2>
        <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(170px, 1fr)); gap: 1rem;">

            <a href="${pageContext.request.contextPath}/products?category=Electronics" class="card" style="text-decoration:none; text-align:center; padding: 1.5rem 1rem;">
                <div style="font-size: 2rem; margin-bottom: 0.5rem;">&#128241;</div>
                <div style="font-weight: 700; color: var(--text);">Electronics</div>
                <div style="font-size: 0.8rem; color: var(--subtext);">10 products</div>
            </a>

            <a href="${pageContext.request.contextPath}/products?category=Clothing" class="card" style="text-decoration:none; text-align:center; padding: 1.5rem 1rem;">
                <div style="font-size: 2rem; margin-bottom: 0.5rem;">&#128085;</div>
                <div style="font-weight: 700; color: var(--text);">Clothing</div>
                <div style="font-size: 0.8rem; color: var(--subtext);">4 products</div>
            </a>

            <a href="${pageContext.request.contextPath}/products?category=Books" class="card" style="text-decoration:none; text-align:center; padding: 1.5rem 1rem;">
                <div style="font-size: 2rem; margin-bottom: 0.5rem;">&#128218;</div>
                <div style="font-weight: 700; color: var(--text);">Books</div>
                <div style="font-size: 0.8rem; color: var(--subtext);">4 products</div>
            </a>

            <a href="${pageContext.request.contextPath}/products?category=Home" class="card" style="text-decoration:none; text-align:center; padding: 1.5rem 1rem;">
                <div style="font-size: 2rem; margin-bottom: 0.5rem;">&#127968;</div>
                <div style="font-weight: 700; color: var(--text);">Home & Garden</div>
                <div style="font-size: 0.8rem; color: var(--subtext);">2 products</div>
            </a>

            <a href="${pageContext.request.contextPath}/products?category=Sports" class="card" style="text-decoration:none; text-align:center; padding: 1.5rem 1rem;">
                <div style="font-size: 2rem; margin-bottom: 0.5rem;">&#127947;</div>
                <div style="font-weight: 700; color: var(--text);">Sports</div>
                <div style="font-size: 0.8rem; color: var(--subtext);">3 products</div>
            </a>

            <a href="${pageContext.request.contextPath}/products?category=Beauty" class="card" style="text-decoration:none; text-align:center; padding: 1.5rem 1rem;">
                <div style="font-size: 2rem; margin-bottom: 0.5rem;">&#128132;</div>
                <div style="font-weight: 700; color: var(--text);">Beauty</div>
                <div style="font-size: 0.8rem; color: var(--subtext);">2 products</div>
            </a>

        </div>
    </div>

    <!-- Features Section -->
    <div style="background: var(--sidebar); border-top: 1px solid var(--border); border-bottom: 1px solid var(--border); padding: 3rem 0;">
        <div class="container">
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 2rem; text-align: center;">
                <div>
                    <div style="font-size: 1.8rem; margin-bottom: 0.5rem;">&#128666;</div>
                    <h3>Free Shipping</h3>
                    <p class="text-muted" style="font-size: 0.85rem;">On all orders above Rs 999</p>
                </div>
                <div>
                    <div style="font-size: 1.8rem; margin-bottom: 0.5rem;">&#128274;</div>
                    <h3>Secure Payments</h3>
                    <p class="text-muted" style="font-size: 0.85rem;">100% secure checkout</p>
                </div>
                <div>
                    <div style="font-size: 1.8rem; margin-bottom: 0.5rem;">&#128257;</div>
                    <h3>30-Day Returns</h3>
                    <p class="text-muted" style="font-size: 0.85rem;">Easy return policy</p>
                </div>
                <div>
                    <div style="font-size: 1.8rem; margin-bottom: 0.5rem;">&#127775;</div>
                    <h3>25+ Products</h3>
                    <p class="text-muted" style="font-size: 0.85rem;">Curated premium catalog</p>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer / Status Bar -->
    <div class="status-bar">
        <span>ShopSwing &copy; 2025 | Java Servlet + JSP + MySQL</span>
        <span>Advanced Java E-Commerce Application</span>
    </div>

    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
