<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Cart — ShopSwing</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <!-- Navbar -->
    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/" class="navbar-brand">&#9889; ShopSwing</a>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/products">Products</a>
            <span class="nav-separator">|</span>
            <a href="${pageContext.request.contextPath}/cart" class="active" style="color:#FBBF24;">Cart (${cartCount})</a>
            <span class="nav-separator">|</span>
            <a href="${pageContext.request.contextPath}/orders" style="color:#34D399;">Orders</a>
            <span class="nav-separator">|</span>
            <a href="#" style="color:#B4D4FF;">Hi, ${sessionScope.user.username}</a>
            <span class="nav-separator">|</span>
            <a href="${pageContext.request.contextPath}/logout" style="color:#B46464;">Logout</a>
        </div>
    </nav>

    <div class="container" style="padding:1.5rem;">
        <!-- Header -->
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:1rem;">
            <h2>&#128722; My Cart</h2>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-dark btn-sm" style="color:var(--accent);">
                &#8592; Continue Shopping
            </a>
        </div>

        <c:choose>
            <c:when test="${empty cartItems}">
                <div class="card fade-in" style="text-align:center; padding:4rem 2rem;">
                    <div style="font-size:3rem;margin-bottom:0.5rem;">&#128722;</div>
                    <h3 style="color:var(--muted);">Your cart is empty</h3>
                    <p style="color:var(--subtext);margin-bottom:1rem;">Start shopping to add items!</p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary btn-lg">Browse Products</a>
                </div>
            </c:when>
            <c:otherwise>
                <div style="display:grid; grid-template-columns:1fr 280px; gap:1.2rem;">
                    <!-- Cart Items -->
                    <div>
                        <c:forEach var="item" items="${cartItems}">
                            <div class="card fade-in" style="display:flex; align-items:center; padding:1rem 1.2rem; margin-bottom:0.6rem;">
                                <!-- Product Image -->
                                <div style="width:60px;height:60px;margin-right:1rem;flex-shrink:0;">
                                    <img src="${fn:startsWith(item.productImageUrl, 'http') ? item.productImageUrl : pageContext.request.contextPath.concat('/').concat(item.productImageUrl)}" 
                                         alt="${item.productName}"
                                         style="width:100%; height:100%; object-fit:cover; border-radius:8px; border:1px solid var(--border);"
                                         onerror="this.src='https://placehold.co/100x100/1e293b/white?text=${item.productName}';">
                                </div>

                                <!-- Product info -->
                                <div style="flex:1;">
                                    <div style="font-weight:700;color:var(--text);">${item.productName}</div>
                                    <div style="font-size:0.8rem;color:var(--amber);">
                                        Rs <fmt:formatNumber value="${item.productPrice}" pattern="#,##0"/> each
                                    </div>
                                </div>

                                <!-- Quantity controls -->
                                <div style="display:flex; align-items:center; gap:0.3rem; margin:0 1rem;">
                                    <form action="${pageContext.request.contextPath}/cart" method="POST" style="margin:0;">
                                        <input type="hidden" name="action" value="decrement">
                                        <input type="hidden" name="productId" value="${item.productId}">
                                        <button type="submit" class="btn btn-dark btn-sm" style="padding:0.3rem 0.6rem;">−</button>
                                    </form>
                                    <span style="font-weight:700;color:var(--text);min-width:2rem;text-align:center;">${item.quantity}</span>
                                    <form action="${pageContext.request.contextPath}/cart" method="POST" style="margin:0;">
                                        <input type="hidden" name="action" value="increment">
                                        <input type="hidden" name="productId" value="${item.productId}">
                                        <button type="submit" class="btn btn-dark btn-sm" style="padding:0.3rem 0.6rem;">+</button>
                                    </form>
                                </div>

                                <!-- Subtotal -->
                                <div style="font-weight:700;color:var(--amber);min-width:100px;text-align:right;">
                                    Rs <fmt:formatNumber value="${item.subtotal}" pattern="#,##0"/>
                                </div>

                                <!-- Remove -->
                                <form action="${pageContext.request.contextPath}/cart" method="POST" style="margin:0 0 0 0.8rem;">
                                    <input type="hidden" name="action" value="remove">
                                    <input type="hidden" name="productId" value="${item.productId}">
                                    <button type="submit" class="btn btn-sm" style="color:var(--red);background:transparent;padding:0.3rem 0.5rem;"
                                            onclick="return confirm('Remove this item?');">✕</button>
                                </form>
                            </div>
                        </c:forEach>
                    </div>

                    <!-- Order Summary -->
                    <div class="card" style="padding:1.5rem; align-self:start; position:sticky; top:80px;">
                        <h3 style="margin-bottom:1rem;">Order Summary</h3>
                        <div style="display:flex;justify-content:space-between;margin-bottom:0.5rem;">
                            <span style="color:var(--subtext);">Items</span>
                            <span style="color:var(--text);font-weight:600;">${cartCount}</span>
                        </div>
                        <div style="display:flex;justify-content:space-between;margin-bottom:0.5rem;">
                            <span style="color:var(--subtext);">Shipping</span>
                            <span style="color:var(--green);font-weight:600;">Free</span>
                        </div>
                        <hr style="border-color:var(--border);margin:0.8rem 0;">
                        <div style="display:flex;justify-content:space-between;margin-bottom:1.2rem;">
                            <span style="font-weight:700;font-size:1.1rem;">Total</span>
                            <span style="font-weight:700;font-size:1.1rem;color:var(--amber);">Rs ${cartTotalFormatted}</span>
                        </div>
                        <a href="${pageContext.request.contextPath}/checkout" class="btn btn-success btn-block btn-lg">
                            Place Order
                        </a>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="status-bar" style="margin-top:2rem;">
        <span>ShopSwing &copy; 2025</span>
        <span>${cartCount} items in cart</span>
    </div>

    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
