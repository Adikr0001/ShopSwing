<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard — ShopSwing</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="navbar-brand">&#9889; Admin Panel</a>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="active">Dashboard</a>
            <span class="nav-separator">|</span>
            <a href="${pageContext.request.contextPath}/admin/products">Products</a>
            <span class="nav-separator">|</span>
            <a href="${pageContext.request.contextPath}/admin/orders">Orders</a>
            <span class="nav-separator">|</span>
            <a href="${pageContext.request.contextPath}/logout" style="color: var(--red);">Logout</a>
        </div>
    </nav>

    <div class="container" style="padding: 2rem 1.5rem;">
        <h1 style="margin-bottom: 2rem;">Dashboard Overview</h1>

        <!-- Stats Grid -->
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1.5rem; margin-bottom: 3rem;">
            <div class="card" style="padding: 1.5rem; text-align: center; border-left: 4px solid var(--accent);">
                <div style="color: var(--subtext); font-size: 0.9rem; margin-bottom: 0.5rem;">TOTAL PRODUCTS</div>
                <div style="font-size: 2rem; font-weight: 700; color: var(--text);">${stats.totalProducts}</div>
            </div>
            <div class="card" style="padding: 1.5rem; text-align: center; border-left: 4px solid var(--green);">
                <div style="color: var(--subtext); font-size: 0.9rem; margin-bottom: 0.5rem;">TOTAL USERS</div>
                <div style="font-size: 2rem; font-weight: 700; color: var(--text);">${stats.totalUsers}</div>
            </div>
            <div class="card" style="padding: 1.5rem; text-align: center; border-left: 4px solid var(--amber);">
                <div style="color: var(--subtext); font-size: 0.9rem; margin-bottom: 0.5rem;">TOTAL ORDERS</div>
                <div style="font-size: 2rem; font-weight: 700; color: var(--text);">${stats.totalOrders}</div>
            </div>
            <div class="card" style="padding: 1.5rem; text-align: center; border-left: 4px solid var(--accent);">
                <div style="color: var(--subtext); font-size: 0.9rem; margin-bottom: 0.5rem;">TOTAL REVENUE</div>
                <div style="font-size: 2rem; font-weight: 700; color: var(--amber);">Rs ${stats.totalRevenue}</div>
            </div>
        </div>

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 2rem;">
            <div>
                <h3>Quick Actions</h3>
                <div class="card" style="padding: 1rem; display: flex; flex-direction: column; gap: 0.8rem;">
                    <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-primary">Add New Product</a>
                    <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-outline">Process Pending Orders</a>
                    <a href="${pageContext.request.contextPath}/" class="btn btn-dark">View Live Store</a>
                </div>
            </div>
            <div>
                <h3>Recent Products</h3>
                <div class="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Price</th>
                                <th>Stock</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="p" items="${recentProducts}" end="4">
                                <tr>
                                    <td>${p.name}</td>
                                    <td>Rs ${p.price}</td>
                                    <td>
                                        <span style="color: ${p.stock < 10 ? 'var(--red)' : 'var(--green)'}">${p.stock}</span>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
