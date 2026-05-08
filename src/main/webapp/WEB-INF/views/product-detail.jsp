<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${product.name} — ShopSwing</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <c:if test="${sessionScope.addedToCart}">
        <div id="cartToast" style="position:fixed; bottom:20px; right:20px; background:var(--green); color:#1e293b; padding:1rem 2rem; border-radius:8px; box-shadow:0 4px 12px rgba(0,0,0,0.15); z-index:1000; font-weight:700; display:flex; align-items:center; gap:0.5rem; transition: opacity 0.5s ease-in-out;">
            <span style="font-size:1.2rem;color:#1e293b;">&#10004;</span> Added to cart successfully!
        </div>
        <script>
            setTimeout(() => {
                const toast = document.getElementById('cartToast');
                if(toast) {
                    toast.style.opacity = '0';
                    setTimeout(() => toast.remove(), 500);
                }
            }, 3000);
        </script>
        <c:remove var="addedToCart" scope="session" />
    </c:if>

    <!-- Navbar -->
    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/" class="navbar-brand">&#9889; ShopSwing</a>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/products">Products</a>
            <span class="nav-separator">|</span>
            <c:if test="${not empty sessionScope.user}">
                <a href="${pageContext.request.contextPath}/cart" style="color:#FBBF24;position:relative;">
                    &#128722; Cart
                    <c:if test="${cartCount > 0}">
                        <span style="position:absolute;top:-8px;right:-12px;background:#ef4444;color:white;font-size:0.65rem;font-weight:700;padding:2px 6px;border-radius:50%;min-width:16px;text-align:center;">${cartCount}</span>
                    </c:if>
                </a>
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
                <img src="${fn:startsWith(product.imageUrl, 'http') ? product.imageUrl : pageContext.request.contextPath.concat('/').concat(product.imageUrl)}"
                     alt="${product.name}"
                     onerror="this.onerror=null; this.src='${product.categoryName == 'Electronics' ? 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&amp;fit=crop&amp;w=900&amp;q=80' : product.categoryName == 'Clothing' ? 'https://images.unsplash.com/photo-1523398002811-999ca8dec234?auto=format&amp;fit=crop&amp;w=900&amp;q=80' : product.categoryName == 'Books' ? 'https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&amp;fit=crop&amp;w=900&amp;q=80' : product.categoryName == 'Sports' ? 'https://images.unsplash.com/photo-1517836357463-d25dfeac3438?auto=format&amp;fit=crop&amp;w=900&amp;q=80' : product.categoryName == 'Beauty' ? 'https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&amp;fit=crop&amp;w=900&amp;q=80' : 'https://images.unsplash.com/photo-1556911220-bff31c812dba?auto=format&amp;fit=crop&amp;w=900&amp;q=80'}';">
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
                    <c:url var="productCategoryUrl" value="/products">
                        <c:param name="category" value="${product.categoryName}" />
                    </c:url>
                    <a href="${productCategoryUrl}" class="btn btn-outline btn-lg">
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
