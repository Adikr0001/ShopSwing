<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Products — ShopSwing</title>
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
        <div class="search-bar">
            <form action="${pageContext.request.contextPath}/products" method="GET" style="display:flex;width:100%;align-items:center;">
                <c:if test="${not empty selectedCategory}">
                    <input type="hidden" name="category" value="${selectedCategory}">
                </c:if>
                <span style="color:#AAB9E1;margin-right:6px;">&#128269;</span>
                <input type="text" name="search" placeholder="Search products..."
                       value="${searchQuery}" style="flex:1;">
            </form>
        </div>
        <div class="nav-links">
            <c:choose>
                <c:when test="${not empty sessionScope.user}">
                    <a href="${pageContext.request.contextPath}/products" class="active">Products</a>
                    <span class="nav-separator">|</span>
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
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-primary btn-sm">Sign In</a>
                    <a href="${pageContext.request.contextPath}/register" class="btn btn-outline btn-sm">Register</a>
                </c:otherwise>
            </c:choose>
        </div>
    </nav>

    <div class="flex">
        <!-- Sidebar -->
        <div class="sidebar">
            <div class="sidebar-header" style="margin-top:0.5rem;">CATEGORIES</div>
            <a href="${pageContext.request.contextPath}/products"
               class="sidebar-link ${empty selectedCategory ? 'active' : ''}">All Products</a>
            <c:forEach var="cat" items="${categories}">
                <c:url var="categoryUrl" value="/products">
                    <c:param name="category" value="${cat.name}" />
                </c:url>
                <a href="${categoryUrl}"
                   class="sidebar-link ${selectedCategory == cat.name ? 'active' : ''}">${cat.name}</a>
            </c:forEach>

            <div class="sidebar-header" style="margin-top:1.5rem;">SORT BY PRICE</div>
            <c:url var="priceAscUrl" value="/products">
                <c:param name="sort" value="price_asc" />
                <c:if test="${not empty selectedCategory}">
                    <c:param name="category" value="${selectedCategory}" />
                </c:if>
                <c:if test="${not empty searchQuery}">
                    <c:param name="search" value="${searchQuery}" />
                </c:if>
            </c:url>
            <a href="${priceAscUrl}"
               class="sidebar-link">Cheapest First</a>
            <c:url var="priceDescUrl" value="/products">
                <c:param name="sort" value="price_desc" />
                <c:if test="${not empty selectedCategory}">
                    <c:param name="category" value="${selectedCategory}" />
                </c:if>
                <c:if test="${not empty searchQuery}">
                    <c:param name="search" value="${searchQuery}" />
                </c:if>
            </c:url>
            <a href="${priceDescUrl}"
               class="sidebar-link">Premium First</a>

            <div class="sidebar-header" style="margin-top:1.5rem;">SORT BY NAME</div>
            <c:url var="nameAscUrl" value="/products">
                <c:param name="sort" value="name_asc" />
                <c:if test="${not empty selectedCategory}">
                    <c:param name="category" value="${selectedCategory}" />
                </c:if>
                <c:if test="${not empty searchQuery}">
                    <c:param name="search" value="${searchQuery}" />
                </c:if>
            </c:url>
            <a href="${nameAscUrl}"
               class="sidebar-link">Name A → Z</a>
            <c:url var="nameDescUrl" value="/products">
                <c:param name="sort" value="name_desc" />
                <c:if test="${not empty selectedCategory}">
                    <c:param name="category" value="${selectedCategory}" />
                </c:if>
                <c:if test="${not empty searchQuery}">
                    <c:param name="search" value="${searchQuery}" />
                </c:if>
            </c:url>
            <a href="${nameDescUrl}"
               class="sidebar-link">Name Z → A</a>

            <!-- Stats Card -->
            <div style="margin-top:1.5rem; background:var(--card); border:1px solid var(--border); border-radius:10px; padding:0.9rem;">
                <div style="font-weight:700;color:var(--green);font-size:0.85rem;margin-bottom:4px;">Min &nbsp; Rs ${minPrice}</div>
                <div style="font-weight:700;color:var(--red);font-size:0.85rem;margin-bottom:4px;">Max &nbsp; Rs ${maxPrice}</div>
                <div style="font-weight:700;color:var(--amber);font-size:0.85rem;">Avg &nbsp; Rs ${avgPrice}</div>
            </div>
        </div>

        <!-- Main Content -->
        <div style="flex:1; padding:0;">
            <!-- Header Bar -->
            <div style="background:var(--card); padding:0.7rem 1.2rem; display:flex; justify-content:space-between; align-items:center; border-bottom:1px solid var(--border);">
                <h2 style="margin:0; font-size:1.1rem;">
                    Product Catalog
                    <c:if test="${not empty selectedCategory}">
                        <span style="color:var(--subtext);font-weight:400;"> — ${selectedCategory}</span>
                    </c:if>
                    <c:if test="${not empty searchQuery}">
                        <span style="color:var(--subtext);font-weight:400;"> — search: "${searchQuery}"</span>
                    </c:if>
                </h2>
                <span style="color:var(--accent);font-weight:700;font-size:0.9rem;">
                    ${productCount} item${productCount != 1 ? 's' : ''}
                </span>
            </div>

            <!-- Product Grid -->
            <div style="padding:1rem;">
                <c:choose>
                    <c:when test="${empty products}">
                        <div style="text-align:center;padding:4rem 1rem;">
                            <div style="font-size:2.5rem;margin-bottom:0.5rem;">&#128533;</div>
                            <h3 style="color:var(--muted);">No products found</h3>
                            <p style="color:var(--subtext);font-size:0.9rem;">Try a different search or category.</p>
                            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary" style="margin-top:1rem;">View All Products</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="product-grid">
                            <c:forEach var="p" items="${products}">
                                <div class="card product-card fade-in"
                                     data-product-url="${pageContext.request.contextPath}/product-detail?id=${p.id}"
                                     role="link"
                                     tabindex="0"
                                     aria-label="View details for ${p.name}"
                                     style="cursor:pointer;">
                                    <!-- Top: Category pill -->
                                    <div style="padding:0.7rem 1rem 0; display:flex; justify-content:space-between; align-items:center;">
                                        <span class="pill pill-${p.categoryName == 'Electronics' ? 'electronics' : p.categoryName == 'Clothing' ? 'clothing' : p.categoryName == 'Books' ? 'books' : p.categoryName == 'Sports' ? 'sports' : p.categoryName == 'Beauty' ? 'beauty' : 'home'}">
                                            ${p.categoryName}
                                        </span>
                                        <span style="font-size:0.75rem;color:var(--subtext);">#${p.id}</span>
                                    </div>

                                    <!-- Product Image -->
                                    <div class="product-image-container">
                                        <img src="${fn:startsWith(p.imageUrl, 'http') ? p.imageUrl : pageContext.request.contextPath.concat('/').concat(p.imageUrl)}"
                                             alt="${p.name}"
                                             onerror="this.onerror=null; this.src='${p.categoryName == 'Electronics' ? 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&amp;fit=crop&amp;w=700&amp;q=80' : p.categoryName == 'Clothing' ? 'https://images.unsplash.com/photo-1523398002811-999ca8dec234?auto=format&amp;fit=crop&amp;w=700&amp;q=80' : p.categoryName == 'Books' ? 'https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&amp;fit=crop&amp;w=700&amp;q=80' : p.categoryName == 'Sports' ? 'https://images.unsplash.com/photo-1517836357463-d25dfeac3438?auto=format&amp;fit=crop&amp;w=700&amp;q=80' : p.categoryName == 'Beauty' ? 'https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&amp;fit=crop&amp;w=700&amp;q=80' : 'https://images.unsplash.com/photo-1556911220-bff31c812dba?auto=format&amp;fit=crop&amp;w=700&amp;q=80'}';">
                                    </div>

                                    <!-- Info -->
                                    <div class="product-info">
                                        <div class="product-name">${p.name}</div>
                                        <div class="product-brand">${p.brand}</div>
                                        <div class="product-short-desc" style="font-size:0.8rem;color:var(--subtext);margin:0.4rem 0;height:2.4rem;overflow:hidden;">
                                            ${p.shortDescription}
                                        </div>
                                        <div class="stars" style="margin:0.3rem 0;">
                                            <c:forEach begin="1" end="5" var="i">
                                                <span class="star ${i <= p.starCount ? 'filled' : ''}">&#9733;</span>
                                            </c:forEach>
                                            <span class="rating-value">${p.rating}</span>
                                        </div>
                                        <div class="product-price">Rs <fmt:formatNumber value="${p.price}" pattern="#,##0"/></div>
                                    </div>

                                    <!-- Actions -->
                                    <div class="product-actions">
                                        <c:choose>
                                            <c:when test="${not empty sessionScope.user}">
                                                <form action="${pageContext.request.contextPath}/cart" method="POST" style="margin:0;">
                                                    <input type="hidden" name="action" value="add">
                                                    <input type="hidden" name="productId" value="${p.id}">
                                                    <button type="submit" class="btn btn-primary btn-sm btn-block">Add to Cart</button>
                                                </form>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="${pageContext.request.contextPath}/login" class="btn btn-primary btn-sm">Login to Buy</a>
                                            </c:otherwise>
                                        </c:choose>
                                        <a href="${pageContext.request.contextPath}/product-detail?id=${p.id}" class="btn btn-dark btn-sm">View Details</a>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Footer -->
            <div class="status-bar">
                <span>Showing ${productCount} / ${totalCount} products
                    <c:if test="${not empty selectedCategory}"> | ${selectedCategory}</c:if>
                </span>
                <span>ShopSwing | Servlet + JSP + MySQL</span>
            </div>
        </div>
    </div>

    <script>
        document.querySelectorAll('.product-card[data-product-url]').forEach(card => {
            const navigateToProduct = () => {
                window.location.href = card.dataset.productUrl;
            };

            card.addEventListener('click', event => {
                if (event.target.closest('a, button, form, input, select, textarea, label')) {
                    return;
                }
                navigateToProduct();
            });

            card.addEventListener('keydown', event => {
                if (event.key === 'Enter' || event.key === ' ') {
                    event.preventDefault();
                    navigateToProduct();
                }
            });
        });
    </script>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
