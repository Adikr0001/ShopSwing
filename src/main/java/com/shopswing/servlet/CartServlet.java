package com.shopswing.servlet;

import com.shopswing.dao.CartDAO;
import com.shopswing.model.CartItem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * Handles shopping cart operations.
 * GET  /cart          → view cart
 * POST /cart          → add / update / remove items
 *   action=add       → add product to cart
 *   action=update    → update quantity
 *   action=remove    → remove from cart
 */
@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private CartDAO cartDAO = new CartDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check login
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        List<CartItem> cartItems = cartDAO.getCartItems(userId);

        // Calculate total
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getSubtotal();
        }

        request.setAttribute("cartItems", cartItems);
        request.setAttribute("cartTotal", total);
        request.setAttribute("cartTotalFormatted", String.format("%,.2f", total));
        request.setAttribute("cartCount", cartItems.size());

        request.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check login
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String action = request.getParameter("action");
        String productIdStr = request.getParameter("productId");

        if (action == null || productIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        try {
            int productId = Integer.parseInt(productIdStr.trim());

            switch (action) {
                case "add":
                    cartDAO.addToCart(userId, productId);
                    // Redirect back to referring page or products
                    String referer = request.getHeader("Referer");
                    if (referer != null && !referer.isEmpty()) {
                        response.sendRedirect(referer);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/products");
                    }
                    return;

                case "update":
                    String qtyStr = request.getParameter("quantity");
                    if (qtyStr != null) {
                        int qty = Integer.parseInt(qtyStr.trim());
                        cartDAO.updateQuantity(userId, productId, qty);
                    }
                    break;

                case "increment":
                    int currentQty = cartDAO.getItemQuantity(userId, productId);
                    cartDAO.updateQuantity(userId, productId, currentQty + 1);
                    break;

                case "decrement":
                    int curQty = cartDAO.getItemQuantity(userId, productId);
                    cartDAO.updateQuantity(userId, productId, curQty - 1);
                    break;

                case "remove":
                    cartDAO.removeFromCart(userId, productId);
                    break;
            }
        } catch (NumberFormatException e) {
            // ignore invalid input
        }

        response.sendRedirect(request.getContextPath() + "/cart");
    }
}
