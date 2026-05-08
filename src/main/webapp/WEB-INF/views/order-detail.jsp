<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setTimeZone value="Asia/Kolkata"/>
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
        </div>
    </nav>

    <div class="container" style="padding:1.5rem; max-width:800px;">

        <!-- Success message if just placed -->
        <c:if test="${justPlaced}">
            <div class="card fade-in" style="padding:2rem;text-align:center;margin-bottom:1.5rem;border-top:4px solid var(--green);background-color:rgba(52, 211, 153, 0.05);">
                <div style="font-size:3.5rem;color:var(--green);margin-bottom:0.5rem;">&#10004;</div>
                <h1 style="color:var(--green);margin:0.5rem 0;">Order Placed Successfully!</h1>
                <p style="color:var(--subtext);font-size:1.1rem;margin-bottom:1.5rem;">Thank you for your purchase. Your order has been received.</p>
                
                <div style="display:inline-block;text-align:left;background:var(--bg);padding:1rem 1.5rem;border-radius:8px;border:1px solid var(--border);">
                    <div style="margin-bottom:0.5rem;">
                        <span style="color:var(--muted);">Order Number:</span>
                        <span style="font-weight:700;margin-left:0.5rem;">#${order.id}</span>
                    </div>
                    <div>
                        <span style="color:var(--muted);">Estimated Delivery:</span>
                        <span style="font-weight:700;color:var(--green);margin-left:0.5rem;">
                            <fmt:formatDate value="${order.estimatedDeliveryDate}" pattern="EEEE, dd MMM yyyy"/>
                        </span>
                    </div>
                </div>
            </div>
        </c:if>

        <!-- Success message if just cancelled -->
        <c:if test="${justCancelled}">
            <div class="card fade-in" style="padding:1.5rem;text-align:center;margin-bottom:1.5rem;border-top:4px solid var(--red);background-color:rgba(239, 68, 68, 0.05);">
                <div style="font-size:2.5rem;color:var(--red);margin-bottom:0.5rem;">&#10006;</div>
                <h2 style="color:var(--red);margin:0.5rem 0;">Order Cancelled</h2>
                <p style="color:var(--subtext);">Your order #${order.id} has been cancelled successfully.</p>
            </div>
        </c:if>

        <!-- Error message if cancel failed -->
        <c:if test="${cancelError}">
            <div class="card fade-in" style="padding:1rem;text-align:center;margin-bottom:1rem;border:1px solid var(--red);background-color:rgba(239, 68, 68, 0.1);">
                <p style="color:var(--red);margin:0;">&#9888; Unable to cancel this order. It may have already been shipped or delivered.</p>
            </div>
        </c:if>

        <!-- Order Header -->
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:1rem;">
            <h2>Order #${order.id}</h2>
            <a href="${pageContext.request.contextPath}/orders" class="btn btn-dark btn-sm" style="color:var(--accent);">&#8592; All Orders</a>
        </div>

        <!-- Order Info -->
        <div class="card fade-in" style="padding:1.5rem; margin-bottom:1rem;">
            <div style="display:grid;grid-template-columns:1fr 1fr 1fr 1fr;gap:1rem;">
                <div>
                    <div style="font-size:0.75rem;color:var(--muted);">Order Date</div>
                    <div style="font-weight:700;"><fmt:formatDate value="${order.createdAt}" pattern="dd MMM yyyy"/></div>
                </div>
                <div>
                    <div style="font-size:0.75rem;color:var(--muted);">Estimated Delivery</div>
                    <div style="font-weight:700;color:var(--green);"><fmt:formatDate value="${order.estimatedDeliveryDate}" pattern="dd MMM"/></div>
                </div>
                <div>
                    <div style="font-size:0.75rem;color:var(--muted);">Status</div>
                    <div><span class="pill" style="background:${order.status == 'Cancelled' ? 'var(--red)' : order.status == 'Delivered' ? 'var(--green)' : order.status == 'Shipped' ? 'var(--amber)' : 'var(--accent)'};">${order.status}</span></div>
                </div>
                <div>
                    <div style="font-size:0.75rem;color:var(--muted);">Payment</div>
                    <div style="font-weight:700;">${order.paymentMethod}</div>
                </div>
            </div>
            <c:if test="${not empty order.shippingAddress}">
                <div style="margin-top:1.5rem;padding-top:1rem;border-top:1px solid var(--border);">
                    <div style="font-size:0.75rem;color:var(--muted);margin-bottom:0.3rem;">Shipping Address</div>
                    <div style="font-weight:600;line-height:1.4;">${order.shippingAddress}</div>
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

        <div style="text-align:center;margin-top:1.5rem;display:flex;justify-content:center;gap:1rem;flex-wrap:wrap;">
            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary btn-lg">Continue Shopping</a>
            
            <!-- Cancel Order Button - only for Placed or Processing orders -->
            <c:if test="${order.status == 'Placed' || order.status == 'Processing'}">
                <form action="${pageContext.request.contextPath}/order-detail" method="POST" style="margin:0;">
                    <input type="hidden" name="action" value="cancel">
                    <input type="hidden" name="orderId" value="${order.id}">
                    <button type="submit" class="btn btn-lg" 
                            style="background:transparent;border:2px solid var(--red);color:var(--red);"
                            onclick="return confirm('Are you sure you want to cancel this order?');">
                        &#10006; Cancel Order
                    </button>
                </form>
            </c:if>
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
