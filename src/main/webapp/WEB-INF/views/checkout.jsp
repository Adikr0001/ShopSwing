<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Checkout — ShopSwing</title>
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
            <a href="#" style="color:#B4D4FF;">Hi, ${sessionScope.user.username}</a>
            <span class="nav-separator">|</span>
            <a href="${pageContext.request.contextPath}/logout" style="color:#B46464;">Logout</a>
        </div>
    </nav>

    <div class="container" style="padding:1.5rem; max-width:900px;">
        <h2 style="margin-bottom:1rem;">&#128230; Checkout</h2>

        <c:if test="${not empty error}">
            <p class="error-msg">${error}</p>
        </c:if>

        <div style="display:grid; grid-template-columns:1fr 300px; gap:1.2rem;">
            <!-- Address Form -->
            <form action="${pageContext.request.contextPath}/checkout" method="POST">
                <div class="card" style="padding:1.5rem; margin-bottom:1rem;">
                    <h3 style="margin-bottom:1rem;">Shipping Address</h3>
                    <div class="form-group">
                        <label class="form-label" for="address">Full Address</label>
                        <textarea class="form-control" id="address" name="address" rows="4"
                                  placeholder="House/Flat, Street, City, State, PIN Code" required></textarea>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="payment">Payment Method</label>
                        <select class="form-control" id="payment" name="payment">
                            <option value="Cash on Delivery">Cash on Delivery</option>
                            <option value="UPI">UPI</option>
                            <option value="Credit Card">Credit Card</option>
                            <option value="Debit Card">Debit Card</option>
                            <option value="Net Banking">Net Banking</option>
                        </select>
                    </div>
                </div>

                <!-- Items Summary -->
                <div class="card" style="padding:1.5rem;">
                    <h3 style="margin-bottom:0.8rem;">Order Items (${cartCount})</h3>
                    <c:forEach var="item" items="${cartItems}">
                        <div style="display:flex;justify-content:space-between;padding:0.5rem 0;border-bottom:1px solid var(--border);">
                            <div>
                                <span style="color:var(--text);">${item.productName}</span>
                                <span style="color:var(--subtext);font-size:0.8rem;"> × ${item.quantity}</span>
                            </div>
                            <span style="color:var(--amber);font-weight:600;">Rs <fmt:formatNumber value="${item.subtotal}" pattern="#,##0"/></span>
                        </div>
                    </c:forEach>
                </div>

                <button type="submit" class="btn btn-success btn-block btn-lg" style="margin-top:1rem;"
                        onclick="return confirm('Confirm your order?');">
                    &#10003; Place Order — Rs ${cartTotal}
                </button>
            </form>

            <!-- Summary Sidebar -->
            <div class="card" style="padding:1.5rem; align-self:start; position:sticky; top:80px;">
                <h3 style="margin-bottom:1rem;">Summary</h3>
                <div style="display:flex;justify-content:space-between;margin-bottom:0.4rem;">
                    <span style="color:var(--subtext);">Items</span>
                    <span>${cartCount}</span>
                </div>
                <div style="display:flex;justify-content:space-between;margin-bottom:0.4rem;">
                    <span style="color:var(--subtext);">Shipping</span>
                    <span style="color:var(--green);">Free</span>
                </div>
                <hr style="border-color:var(--border);margin:0.7rem 0;">
                <div style="display:flex;justify-content:space-between;">
                    <span style="font-weight:700;font-size:1.1rem;">Total</span>
                    <span style="font-weight:700;font-size:1.1rem;color:var(--amber);">Rs ${cartTotal}</span>
                </div>
            </div>
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
