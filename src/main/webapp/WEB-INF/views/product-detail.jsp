<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${product.name} — ShopSwing</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <!-- Navbar -->
    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/" class="navbar-brand">&#9889; ShopSwing</a>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/products">Products</a>
            <span class="nav-separator">|</span>
            <c:if test="${not empty sessionScope.user}">
                <a href="${pageContext.request.contextPath}/cart" style="color:#FBBF24;">Cart</a>
                <span class="nav-separator">|</span>
                <a href="${pageContext.request.contextPath}/orders" style="color:#34D399;">Orders</a>
                <span class="nav-separator">|</span>
                <a href="#" style="color:#B4D4FF;">Hi, ${sessionScope.user.username}</a>
                <span class="nav-separator">|</span>
                <a href="${pageContext.request.contextPath}/logout" style="color:#B46464;">Logout</a>
            </c:if>
            <c:if test="${empty sessionScope.user}">
                <a href="${pageContext.request.contextPath}/login" class="btn btn-primary btn-sm">Sign In</a>
            </c:if>
        </div>
    </nav>

    <div class="container" style="padding:1.5rem;">
        <!-- Back button -->
        <div style="margin-bottom:1rem;">
            <a href="${pageContext.request.contextPath}/products" class="btn btn-dark btn-sm" style="color:var(--accent);">
                &#8592; Back to Catalog
            </a>
        </div>

        <div class="fade-in" style="display:grid; grid-template-columns: 300px 1fr; gap:1.5rem;">
            <!-- Left: Product Image Area -->
            <div class="product-image-large">
                <img src="${pageContext.request.contextPath}/${product.imageUrl}" 
                     alt="${product.name}"
                     onerror="this.src='https://placehold.co/600x600/1e293b/white?text=${product.name}';">
            </div>

            <!-- Right: Product details -->
            <div class="card" style="padding:2rem;">
                <h1 style="font-size:1.5rem; margin-bottom:0.5rem;">${product.name}</h1>

                <div style="display:flex; align-items:center; gap:0.8rem; margin-bottom:0.8rem;">
                    <span class="pill pill-${product.categoryName == 'Electronics' ? 'electronics' : product.categoryName == 'Clothing' ? 'clothing' : product.categoryName == 'Books' ? 'books' : product.categoryName == 'Sports' ? 'sports' : product.categoryName == 'Beauty' ? 'beauty' : 'home'}">
                        ${product.categoryName}
                    </span>
                    <span style="color:var(--subtext);font-size:0.9rem;">by ${product.brand}</span>
                </div>

                <!-- Stars -->
                <div style="margin-bottom:1rem;">
                    <div class="stars">
                        <c:forEach begin="1" end="5" var="i">
                            <span class="star ${i <= product.starCount ? 'filled' : ''}" style="font-size:1.1rem;">&#9733;</span>
                        </c:forEach>
                        <span class="rating-value" style="font-size:0.95rem;">${product.rating} / 5.0</span>
                    </div>
                </div>

                <!-- Price -->
                <div style="font-size:1.8rem; font-weight:700; color:var(--amber); margin-bottom:1.2rem;">
                    Rs <fmt:formatNumber value="${product.price}" pattern="#,##0.00"/>
                </div>

                <hr style="border-color:var(--border); margin-bottom:1rem;">

                <!-- Description -->
                <h3 style="color:var(--subtext); font-size:0.85rem; margin-bottom:0.3rem;">Description</h3>
                <p style="color:var(--text); margin-bottom:1.2rem; line-height:1.7;">${product.description}</p>

                <!-- Details Grid (Dynamic Specifications) -->
                <h3 style="color:var(--subtext); font-size:0.85rem; margin-bottom:0.3rem; margin-top:1.5rem;">Specifications</h3>
                <div style="display:grid; grid-template-columns:1fr 1fr; gap:0.8rem; margin-bottom:1.5rem;">
                    <div style="background:var(--bg); padding:0.7rem; border-radius:8px;">
                        <div style="font-size:0.75rem;color:var(--muted);">Product ID</div>
                        <div style="font-weight:700;color:var(--text);">#${product.id}</div>
                    </div>
                    <div style="background:var(--bg); padding:0.7rem; border-radius:8px;">
                        <div style="font-size:0.75rem;color:var(--muted);">In Stock</div>
                        <div style="font-weight:700;color:${product.stock > 0 ? 'var(--green)' : 'var(--red)'};">${product.stock} units</div>
                    </div>
                    <c:forEach var="entry" items="${product.specificationsMap}">
                        <div style="background:var(--bg); padding:0.7rem; border-radius:8px;">
                            <div style="font-size:0.75rem;color:var(--muted);">${entry.key}</div>
                            <div style="font-weight:700;color:var(--text);">${entry.value}</div>
                        </div>
                    </c:forEach>
                    <!-- Fallback for base details if no specs -->
                    <c:if test="${empty product.specificationsMap}">
                        <div style="background:var(--bg); padding:0.7rem; border-radius:8px;">
                            <div style="font-size:0.75rem;color:var(--muted);">Brand</div>
                            <div style="font-weight:700;color:var(--text);">${product.brand}</div>
                        </div>
                        <div style="background:var(--bg); padding:0.7rem; border-radius:8px;">
                            <div style="font-size:0.75rem;color:var(--muted);">Category</div>
                            <div style="font-weight:700;color:var(--text);">${product.categoryName}</div>
                        </div>
                    </c:if>
                </div>

                <!-- Action Buttons -->
                <div style="display:flex; gap:0.8rem;">
                    <c:choose>
                        <c:when test="${not empty sessionScope.user}">
                            <form action="${pageContext.request.contextPath}/cart" method="POST" style="margin:0;">
                                <input type="hidden" name="action" value="add">
                                <input type="hidden" name="productId" value="${product.id}">
                                <button type="submit" class="btn btn-primary btn-lg">&#128722; Add to Cart</button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/login" class="btn btn-primary btn-lg">Login to Buy</a>
                        </c:otherwise>
                    </c:choose>
                    <a href="${pageContext.request.contextPath}/products?category=${product.categoryName}" class="btn btn-outline btn-lg">
                        More in ${product.categoryName}
                    </a>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <div class="status-bar" style="margin-top:2rem;">
        <span>ShopSwing &copy; 2025</span>
        <span>Product #${product.id}</span>
    </div>

    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
