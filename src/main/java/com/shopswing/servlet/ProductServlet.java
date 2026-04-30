package com.shopswing.servlet;

import com.shopswing.dao.CategoryDAO;
import com.shopswing.dao.ProductDAO;
import com.shopswing.model.Category;
import com.shopswing.model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

/**
 * Handles product listing with category filter and search.
 * GET /products              → all products
 * GET /products?category=X   → filter by category
 * GET /products?search=X     → search products
 * GET /products?sort=X       → sort products
 */
@WebServlet("/products")
public class ProductServlet extends HttpServlet {

    private ProductDAO productDAO = new ProductDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String category = request.getParameter("category");
        String search = request.getParameter("search");
        String sort = request.getParameter("sort");

        // Trim inputs
        if (category != null) category = category.trim();
        if (search != null) search = search.trim();
        if (sort != null) sort = sort.trim();

        // Fetch products based on filters
        List<Product> products;

        if (category != null && !category.isEmpty() && search != null && !search.isEmpty()) {
            products = productDAO.getProductsByCategoryAndSearch(category, search);
        } else if (category != null && !category.isEmpty()) {
            products = productDAO.getProductsByCategory(category);
        } else if (search != null && !search.isEmpty()) {
            products = productDAO.searchProducts(search);
        } else {
            products = productDAO.getAllProducts();
        }

        // Apply sort after filtering so sort works with category/search combinations.
        if (sort != null && !sort.isEmpty()) {
            applySort(products, sort);
        }

        // Fetch all categories for sidebar
        List<Category> categories = categoryDAO.getAllCategories();

        // Calculate stats
        double minPrice = products.stream().mapToDouble(Product::getPrice).min().orElse(0);
        double maxPrice = products.stream().mapToDouble(Product::getPrice).max().orElse(0);
        double avgPrice = products.stream().mapToDouble(Product::getPrice).average().orElse(0);
        int totalCount = productDAO.getProductCount();

        // Set attributes for JSP
        request.setAttribute("products", products);
        request.setAttribute("categories", categories);
        request.setAttribute("selectedCategory", category);
        request.setAttribute("searchQuery", search);
        request.setAttribute("selectedSort", sort);
        request.setAttribute("productCount", products.size());
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("minPrice", String.format("%,.0f", minPrice));
        request.setAttribute("maxPrice", String.format("%,.0f", maxPrice));
        request.setAttribute("avgPrice", String.format("%,.0f", avgPrice));

        request.getRequestDispatcher("/WEB-INF/views/products.jsp").forward(request, response);
    }

    private void applySort(List<Product> products, String sort) {
        switch (sort) {
            case "price_asc":
                products.sort(Comparator.comparingDouble(Product::getPrice));
                break;
            case "price_desc":
                products.sort(Comparator.comparingDouble(Product::getPrice).reversed());
                break;
            case "name_asc":
                products.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER));
                break;
            case "name_desc":
                products.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER).reversed());
                break;
            case "rating_desc":
                products.sort(Comparator.comparingDouble(Product::getRating).reversed());
                break;
            default:
                break;
        }
    }
}
