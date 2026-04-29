<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 — Server Error | ShopSwing</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="auth-wrapper">
        <div class="auth-card" style="text-align: center;">
            <div style="font-size: 4rem; margin-bottom: 0.5rem;">&#9888;&#65039;</div>
            <h1 style="color: var(--red);">500</h1>
            <p class="subtitle">Internal Server Error</p>
            <p style="color: var(--subtext); margin-bottom: 1.5rem;">
                Something went wrong on our end. Please try again later.
            </p>
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary btn-lg btn-block">
                Back to Home
            </a>
        </div>
    </div>
</body>
</html>
