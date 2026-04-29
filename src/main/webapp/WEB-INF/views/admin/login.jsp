<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Login — ShopSwing</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="auth-wrapper">
        <div class="auth-card fade-in">
            <h1 style="color: var(--accent);">&#9889; Admin Panel</h1>
            <p class="subtitle">Authorized Access Only</p>

            <c:if test="${not empty error}">
                <p class="error-msg">${error}</p>
            </c:if>

            <form action="${pageContext.request.contextPath}/admin/login" method="POST">
                <div class="form-group">
                    <label class="form-label" for="username">Admin Username</label>
                    <input type="text" class="form-control" id="username" name="username" required autofocus>
                </div>

                <div class="form-group">
                    <label class="form-label" for="password">Password</label>
                    <input type="password" class="form-control" id="password" name="password" required>
                </div>

                <button type="submit" class="btn btn-primary btn-block btn-lg" style="margin-top: 0.5rem;">
                    Login to Dashboard
                </button>
            </form>
            
            <div style="margin-top: 1rem; text-align: center;">
                <a href="${pageContext.request.contextPath}/" style="font-size: 0.85rem; color: var(--subtext);">Back to Store</a>
            </div>
        </div>
    </div>
</body>
</html>
