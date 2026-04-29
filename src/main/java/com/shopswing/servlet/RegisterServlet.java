package com.shopswing.servlet;

import com.shopswing.dao.UserDAO;
import com.shopswing.model.User;
import com.shopswing.utils.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Handles user registration.
 * GET  /register → shows register form
 * POST /register → processes registration
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // If already logged in, redirect to home
        if (request.getSession(false) != null &&
            request.getSession().getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get form parameters
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Trim inputs
        if (username != null) username = username.trim();
        if (email != null) email = email.trim();
        if (password != null) password = password.trim();
        if (confirmPassword != null) confirmPassword = confirmPassword.trim();

        // Server-side validation
        String error = null;

        if (username == null || username.isEmpty() ||
            email == null || email.isEmpty() ||
            password == null || password.isEmpty() ||
            confirmPassword == null || confirmPassword.isEmpty()) {
            error = "All fields are required.";
        } else if (username.length() < 3) {
            error = "Username must be at least 3 characters.";
        } else if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            error = "Please enter a valid email address.";
        } else if (password.length() < 4) {
            error = "Password must be at least 4 characters.";
        } else if (!password.equals(confirmPassword)) {
            error = "Passwords do not match.";
        } else if (userDAO.usernameExists(username)) {
            error = "Username is already taken.";
        } else if (userDAO.emailExists(email)) {
            error = "Email is already registered.";
        }

        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("username", username);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }

        // Hash password and register
        String passwordHash = PasswordUtil.hashPassword(password);
        User user = new User(username, email, passwordHash);
        int userId = userDAO.registerUser(user);

        if (userId > 0) {
            // Auto-login after registration
            user.setId(userId);
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userId", userId);
            response.sendRedirect(request.getContextPath() + "/");
        } else {
            request.setAttribute("error", "Registration failed. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        }
    }
}
