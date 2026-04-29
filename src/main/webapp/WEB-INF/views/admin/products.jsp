<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Products — ShopSwing</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="navbar-brand">&#9889; Admin Panel</a>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
            <span class="nav-separator">|</span>
            <a href="${pageContext.request.contextPath}/admin/products" class="active">Products</a>
            <span class="nav-separator">|</span>
            <a href="${pageContext.request.contextPath}/admin/orders">Orders</a>
            <span class="nav-separator">|</span>
            <a href="${pageContext.request.contextPath}/logout" style="color: var(--red);">Logout</a>
        </div>
    </nav>

    <div class="container" style="padding: 2rem 1.5rem;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
            <h1>Manage Products</h1>
            <a href="#productForm" class="btn btn-success">Add New Product</a>
        </div>

        <!-- Product Table -->
        <div class="table-wrapper fade-in">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Category</th>
                        <th>Price</th>
                        <th>Stock</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="p" items="${products}">
                        <tr>
                            <td>${p.id}</td>
                            <td><strong>${p.name}</strong></td>
                            <td>${p.categoryName}</td>
                            <td class="text-amber">Rs ${p.price}</td>
                            <td class="${p.stock < 10 ? 'text-red' : 'text-green'}">${p.stock}</td>
                            <td style="display: flex; gap: 0.5rem;">
                                <a href="${pageContext.request.contextPath}/admin/products?action=edit&id=${p.id}#productForm" class="btn btn-sm btn-dark">Edit</a>
                                <form action="${pageContext.request.contextPath}/admin/products" method="POST" onsubmit="return confirm('Delete this product?');">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="id" value="${p.id}">
                                    <button type="submit" class="btn btn-sm btn-danger">Delete</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <!-- Add/Edit Form -->
        <div id="productForm" style="margin-top: 4rem; padding-top: 2rem; border-top: 1px solid var(--border);">
            <h2>${product != null ? 'Edit Product' : 'Add New Product'}</h2>
            <div class="card" style="padding: 2rem; margin-top: 1.5rem;">
                <form action="${pageContext.request.contextPath}/admin/products" method="POST">
                    <input type="hidden" name="id" value="${product.id}">
                    
                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem;">
                        <div class="form-group">
                            <label class="form-label">Product Name</label>
                            <input type="text" name="name" class="form-control" value="${product.name}" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Category</label>
                            <select name="categoryId" class="form-control" required>
                                <c:forEach var="cat" items="${categories}">
                                    <option value="${cat.id}" ${product.categoryId == cat.id ? 'selected' : ''}>${cat.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Price (Rs)</label>
                            <input type="number" step="0.01" name="price" class="form-control" value="${product.price}" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Stock Quantity</label>
                            <input type="number" name="stock" class="form-control" value="${product.stock}" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Brand</label>
                            <input type="text" name="brand" class="form-control" value="${product.brand}" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Rating (0-5)</label>
                            <input type="number" step="0.1" max="5" name="rating" class="form-control" value="${product.rating}" required>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Image URL (relative to webapp context, e.g., images/product.jpg)</label>
                        <input type="text" name="imageUrl" class="form-control" value="${product.imageUrl}">
                    </div>

                    <div class="form-group">
                        <label class="form-label">Description</label>
                        <textarea name="description" class="form-control" rows="4" required>${product.description}</textarea>
                    </div>

                    <div style="display: flex; gap: 1rem; margin-top: 1rem;">
                        <button type="submit" class="btn btn-primary btn-lg">${product != null ? 'Update Product' : 'Add Product'}</button>
                        <c:if test="${product != null}">
                            <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-outline btn-lg">Cancel</a>
                        </c:if>
                    </div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>
