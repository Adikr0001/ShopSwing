<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Orders — ShopSwing</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/" class="navbar-brand">&#9889; ShopSwing</a>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/products">Products</a>
            <span class="nav-separator">|</span>
            <a href="${pageContext.request.contextPath}/cart" style="color:#FBBF24;">Cart</a>
            <span class="nav-separator">|</span>
            <a href="${pageContext.request.contextPath}/orders" class="active" style="color:#34D399;">Orders</a>
            <span class="nav-separator">|</span>
            <a href="#" style="color:#B4D4FF;">Hi, ${sessionScope.user.username}</a>
            <span class="nav-separator">|</span>
            <a href="${pageContext.request.contextPath}/logout" style="color:#B46464;">Logout</a>
        </div>
    </nav>

    <div class="container" style="padding:1.5rem;">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:1rem;">
            <h2>&#128230; My Orders</h2>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-dark btn-sm" style="color:var(--accent);">&#8592; Back to Catalog</a>
        </div>

        <c:choose>
            <c:when test="${empty orders}">
                <div class="card fade-in" style="text-align:center;padding:4rem 2rem;">
                    <div style="font-size:3rem;margin-bottom:0.5rem;">&#128230;</div>
                    <h3 style="color:var(--muted);">No orders yet</h3>
                    <p style="color:var(--subtext);margin-bottom:1rem;">Place your first order today!</p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary btn-lg">Browse Products</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-wrapper fade-in">
                    <table>
                        <thead>
                            <tr>
                                <th>Order ID</th>
                                <th>Date & Time</th>
                                <th>Items</th>
                                <th>Total</th>
                                <th>Payment</th>
                                <th>Status</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="o" items="${orders}">
                                <tr>
                                    <td style="font-weight:700;">#${o.id}</td>
                                    <td><fmt:formatDate value="${o.createdAt}" pattern="dd MMM yyyy, hh:mm a"/></td>
                                    <td>${o.itemCount} items</td>
                                    <td style="color:var(--amber);font-weight:700;">Rs <fmt:formatNumber value="${o.totalAmount}" pattern="#,##0.00"/></td>
                                    <td>${o.paymentMethod}</td>
                                    <td>
                                        <span class="pill" style="background:${o.status == 'Placed' ? 'var(--accent)' : o.status == 'Delivered' ? 'var(--green)' : o.status == 'Cancelled' ? 'var(--red)' : 'var(--amber)'};">
                                            ${o.status}
                                        </span>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/order-detail?id=${o.id}" class="btn btn-dark btn-sm">View</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <div style="color:var(--muted);font-size:0.8rem;margin-top:0.6rem;padding-left:0.5rem;">
                    Tip: Click "View" to see order details.
                </div>
            </c:otherwise>
        </c:choose>
    </div>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
