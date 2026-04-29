<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Order #${order.id} — ShopSwing</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/" class="navbar-brand">&#9889; ShopSwing</a>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/products">Products</a>
            <span class="nav-separator">|</span>
            <a href="${pageContext.request.contextPath}/orders" style="color:#34D399;">Orders</a>
            <span class="nav-separator">|</span>
            <a href="#" style="color:#B4D4FF;">Hi, ${sessionScope.user.username}</a>
            <span class="nav-separator">|</span>
            <a href="${pageContext.request.contextPath}/logout" style="color:#B46464;">Logout</a>
        </div>
    </nav>

    <div class="container" style="padding:1.5rem; max-width:800px;">

        <!-- Success message if just placed -->
        <c:if test="${justPlaced}">
            <div class="card fade-in" style="padding:1.5rem;text-align:center;margin-bottom:1.2rem;border-color:var(--green);">
                <div style="font-size:2.5rem;">&#10003;</div>
                <h2 style="color:var(--green);margin:0.3rem 0;">Order Placed Successfully!</h2>
                <p style="color:var(--subtext);">Thank you for shopping with ShopSwing.</p>
            </div>
        </c:if>

        <!-- Order Header -->
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:1rem;">
            <h2>Order #${order.id}</h2>
            <a href="${pageContext.request.contextPath}/orders" class="btn btn-dark btn-sm" style="color:var(--accent);">&#8592; All Orders</a>
        </div>

        <!-- Order Info -->
        <div class="card fade-in" style="padding:1.5rem; margin-bottom:1rem;">
            <div style="display:grid;grid-template-columns:1fr 1fr 1fr;gap:1rem;">
                <div>
                    <div style="font-size:0.75rem;color:var(--muted);">Date</div>
                    <div style="font-weight:700;"><fmt:formatDate value="${order.createdAt}" pattern="dd MMM yyyy, hh:mm a"/></div>
                </div>
                <div>
                    <div style="font-size:0.75rem;color:var(--muted);">Status</div>
                    <div><span class="pill" style="background:var(--accent);">${order.status}</span></div>
                </div>
                <div>
                    <div style="font-size:0.75rem;color:var(--muted);">Payment</div>
                    <div style="font-weight:700;">${order.paymentMethod}</div>
                </div>
            </div>
            <c:if test="${not empty order.shippingAddress}">
                <div style="margin-top:1rem;">
                    <div style="font-size:0.75rem;color:var(--muted);">Shipping Address</div>
                    <div style="font-weight:600;">${order.shippingAddress}</div>
                </div>
            </c:if>
        </div>

        <!-- Order Items Table -->
        <div class="table-wrapper fade-in">
            <table>
                <thead>
                    <tr>
                        <th>Product</th>
                        <th>Price</th>
                        <th>Qty</th>
                        <th>Subtotal</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${order.items}">
                        <tr>
                            <td style="font-weight:600;">${item.productName}</td>
                            <td>Rs <fmt:formatNumber value="${item.price}" pattern="#,##0"/></td>
                            <td>${item.quantity}</td>
                            <td style="color:var(--amber);font-weight:700;">Rs <fmt:formatNumber value="${item.subtotal}" pattern="#,##0"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <!-- Grand Total -->
        <div class="card" style="padding:1rem 1.5rem;margin-top:0.8rem;display:flex;justify-content:space-between;align-items:center;">
            <span style="font-weight:700;font-size:1.1rem;">Grand Total</span>
            <span style="font-weight:700;font-size:1.3rem;color:var(--amber);">Rs <fmt:formatNumber value="${order.totalAmount}" pattern="#,##0.00"/></span>
        </div>

        <div style="text-align:center;margin-top:1.5rem;">
            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary btn-lg">Continue Shopping</a>
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
