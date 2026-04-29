package com.shopswing.servlet;

import com.shopswing.dao.OrderDAO;
import com.shopswing.model.Order;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * Displays order history and order details.
 * GET /orders            → list all user orders
 * GET /order-detail?id=X → show specific order details
 */
@WebServlet(urlPatterns = {"/orders", "/order-detail"})
public class OrderServlet extends HttpServlet {

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
        String path = request.getServletPath();

        if ("/order-detail".equals(path)) {
            // Show single order detail
            String idStr = request.getParameter("id");
            if (idStr == null) {
                response.sendRedirect(request.getContextPath() + "/orders");
                return;
            }
            try {
                int orderId = Integer.parseInt(idStr.trim());
                Order order = orderDAO.getOrderById(orderId);
                if (order == null || order.getUserId() != userId) {
                    response.sendRedirect(request.getContextPath() + "/orders");
                    return;
                }
                request.setAttribute("order", order);
                // Check if just placed
                String placed = request.getParameter("placed");
                if ("true".equals(placed)) {
                    request.setAttribute("justPlaced", true);
                }
                request.getRequestDispatcher("/WEB-INF/views/order-detail.jsp").forward(request, response);
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/orders");
            }
        } else {
            // Show order history
            List<Order> orders = orderDAO.getOrdersByUser(userId);
            request.setAttribute("orders", orders);
            request.getRequestDispatcher("/WEB-INF/views/orders.jsp").forward(request, response);
        }
    }
}
