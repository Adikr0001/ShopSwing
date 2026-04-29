<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In — ShopSwing</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="auth-wrapper">
        <div class="auth-card fade-in">
            <h1>&#9889; ShopSwing</h1>
            <p class="subtitle">Sign in to your account</p>

            <!-- Error Message -->
            <c:if test="${not empty error}">
                <p class="error-msg">${error}</p>
            </c:if>
            <c:if test="${not empty success}">
                <p class="success-msg">${success}</p>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="POST" id="loginForm">
                <div class="form-group">
                    <label class="form-label" for="username">Username</label>
                    <input type="text" class="form-control" id="username" name="username"
                           placeholder="Enter your username"
                           value="${username}" required autofocus>
                </div>

                <div class="form-group">
                    <label class="form-label" for="password">Password</label>
                    <input type="password" class="form-control" id="password" name="password"
                           placeholder="Enter your password" required>
                </div>

                <button type="submit" class="btn btn-primary btn-block btn-lg" style="margin-top: 0.5rem;">
                    Sign In
                </button>
            </form>

            <div style="margin-top: 1rem;">
                <a href="${pageContext.request.contextPath}/register"
                   class="btn btn-dark btn-block" style="color: var(--accent);">
                    Create an Account
                </a>
            </div>
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
