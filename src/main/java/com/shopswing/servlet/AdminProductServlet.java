package com.shopswing.servlet;

import com.shopswing.dao.CategoryDAO;
import com.shopswing.dao.ProductDAO;
import com.shopswing.model.Category;
import com.shopswing.model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/products")
public class AdminProductServlet extends HttpServlet {

    private ProductDAO productDAO = new ProductDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }

        String action = request.getParameter("action");
        if ("edit".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            Product product = productDAO.getProductById(id);
            request.setAttribute("product", product);
        }
        
        List<Product> products = productDAO.getAllProducts();
        List<Category> categories = categoryDAO.getAllCategories();
        
        request.setAttribute("products", products);
        request.setAttribute("categories", categories);
        request.getRequestDispatcher("/WEB-INF/views/admin/products.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }

        String action = request.getParameter("action");
        
        if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            productDAO.deleteProduct(id);
        } else {
            // Add or Update
            String idStr = request.getParameter("id");
            Product p = new Product();
            if (idStr != null && !idStr.isEmpty()) {
                p.setId(Integer.parseInt(idStr));
            }
            p.setName(request.getParameter("name"));
            p.setCategoryId(Integer.parseInt(request.getParameter("categoryId")));
            p.setPrice(Double.parseDouble(request.getParameter("price")));
            p.setDescription(request.getParameter("description"));
            p.setBrand(request.getParameter("brand"));
            p.setRating(Double.parseDouble(request.getParameter("rating")));
            p.setStock(Integer.parseInt(request.getParameter("stock")));
            p.setImageUrl(request.getParameter("imageUrl"));

            if (p.getId() > 0) {
                productDAO.updateProduct(p);
            } else {
                productDAO.addProduct(p);
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/products");
    }
}
