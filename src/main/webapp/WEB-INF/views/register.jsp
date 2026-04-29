<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Account — ShopSwing</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="auth-wrapper">
        <div class="auth-card fade-in">
            <h1 style="color: var(--green);">Create Account</h1>
            <p class="subtitle">Join ShopSwing today</p>

            <!-- Error Message -->
            <c:if test="${not empty error}">
                <p class="error-msg">${error}</p>
            </c:if>

            <form action="${pageContext.request.contextPath}/register" method="POST" id="registerForm">
                <div class="form-group">
                    <label class="form-label" for="username">Username</label>
                    <input type="text" class="form-control" id="username" name="username"
                           placeholder="Choose a username" minlength="3"
                           value="${username}" required autofocus>
                </div>

                <div class="form-group">
                    <label class="form-label" for="email">Email</label>
                    <input type="email" class="form-control" id="email" name="email"
                           placeholder="Enter your email"
                           value="${email}" required>
                </div>

                <div class="form-group">
                    <label class="form-label" for="password">Password</label>
                    <input type="password" class="form-control" id="password" name="password"
                           placeholder="Create a password (min 4 chars)" minlength="4" required>
                </div>

                <div class="form-group">
                    <label class="form-label" for="confirmPassword">Confirm Password</label>
                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword"
                           placeholder="Confirm your password" minlength="4" required>
                </div>

                <button type="submit" class="btn btn-success btn-block btn-lg" style="margin-top: 0.5rem;">
                    Register
                </button>
            </form>

            <div style="margin-top: 1rem;">
                <a href="${pageContext.request.contextPath}/login"
                   class="btn btn-dark btn-block" style="color: var(--accent);">
                    Back to Login
                </a>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <script>
        // Client-side password match validation
        document.getElementById('registerForm').addEventListener('submit', function(e) {
            var pw = document.getElementById('password').value;
            var cpw = document.getElementById('confirmPassword').value;
            if (pw !== cpw) {
                e.preventDefault();
                showToast('Passwords do not match!', 'error');
                document.getElementById('confirmPassword').focus();
            }
        });
    </script>
</body>
</html>
