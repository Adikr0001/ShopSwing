package com.shopswing.servlet;

import com.shopswing.dao.CartDAO;
import com.shopswing.dao.OrderDAO;
import com.shopswing.model.CartItem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * Handles checkout and order placement.
 * GET  /checkout → shows checkout form with cart summary
 * POST /checkout → places the order
 */
@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private CartDAO cartDAO = new CartDAO();
    private OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        List<CartItem> items = cartDAO.getCartItems(userId);

        if (items.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        double total = 0;
        for (CartItem item : items) total += item.getSubtotal();

        request.setAttribute("cartItems", items);
        request.setAttribute("cartTotal", String.format("%,.2f", total));
        request.setAttribute("cartCount", items.size());
        request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String address = request.getParameter("address");
        String payment = request.getParameter("payment");

        if (address == null || address.trim().isEmpty()) {
            address = "Not provided";
        }

        List<CartItem> items = cartDAO.getCartItems(userId);
        if (items.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        int orderId = orderDAO.placeOrder(userId, items, address.trim(), payment);

        if (orderId > 0) {
            request.setAttribute("orderId", orderId);
            response.sendRedirect(request.getContextPath() + "/order-detail?id=" + orderId + "&placed=true");
        } else {
            request.setAttribute("error", "Failed to place order. Please try again.");
            doGet(request, response);
        }
    }
}
