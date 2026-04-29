package com.shopswing.servlet;

import com.shopswing.dao.AdminDAO;
import com.shopswing.dao.OrderDAO;
import com.shopswing.dao.ProductDAO;
import com.shopswing.model.Order;
import com.shopswing.model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private AdminDAO adminDAO = new AdminDAO();
    private ProductDAO productDAO = new ProductDAO();
    private OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }

        Map<String, Integer> stats = adminDAO.getDashboardStats();
        List<Product> recentProducts = productDAO.getAllProducts(); // In a real app, maybe limit to 5
        
        request.setAttribute("stats", stats);
        request.setAttribute("recentProducts", recentProducts);
        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }
}
