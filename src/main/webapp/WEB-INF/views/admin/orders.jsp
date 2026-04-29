<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Orders — ShopSwing</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="navbar-brand">&#9889; Admin Panel</a>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
            <span class="nav-separator">|</span>
            <a href="${pageContext.request.contextPath}/admin/products">Products</a>
            <span class="nav-separator">|</span>
            <a href="${pageContext.request.contextPath}/admin/orders" class="active">Orders</a>
            <span class="nav-separator">|</span>
            <a href="${pageContext.request.contextPath}/logout" style="color: var(--red);">Logout</a>
        </div>
    </nav>

    <div class="container" style="padding: 2rem 1.5rem;">
        <h1 style="margin-bottom: 2rem;">Manage Orders</h1>

        <div class="table-wrapper fade-in">
            <table>
                <thead>
                    <tr>
                        <th>Order ID</th>
                        <th>User</th>
                        <th>Date</th>
                        <th>Total</th>
                        <th>Status</th>
                        <th>Update Status</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="o" items="${orders}">
                        <tr>
                            <td>#${o.id}</td>
                            <td>${o.username}</td>
                            <td><fmt:formatDate value="${o.createdAt}" pattern="dd MMM yyyy, HH:mm"/></td>
                            <td class="text-amber">Rs ${o.totalAmount}</td>
                            <td>
                                <span class="pill" style="background: ${o.status == 'Placed' ? 'var(--accent)' : o.status == 'Delivered' ? 'var(--green)' : o.status == 'Cancelled' ? 'var(--red)' : 'var(--amber)'}">
                                    ${o.status}
                                </span>
                            </td>
                            <td>
                                <form action="${pageContext.request.contextPath}/admin/orders" method="POST" style="display: flex; gap: 0.5rem;">
                                    <input type="hidden" name="orderId" value="${o.id}">
                                    <select name="status" class="form-control" style="padding: 0.2rem 0.5rem; font-size: 0.85rem;">
                                        <option value="Placed" ${o.status == 'Placed' ? 'selected' : ''}>Placed</option>
                                        <option value="Processing" ${o.status == 'Processing' ? 'selected' : ''}>Processing</option>
                                        <option value="Shipped" ${o.status == 'Shipped' ? 'selected' : ''}>Shipped</option>
                                        <option value="Delivered" ${o.status == 'Delivered' ? 'selected' : ''}>Delivered</option>
                                        <option value="Cancelled" ${o.status == 'Cancelled' ? 'selected' : ''}>Cancelled</option>
                                    </select>
                                    <button type="submit" class="btn btn-sm btn-primary">Update</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>
