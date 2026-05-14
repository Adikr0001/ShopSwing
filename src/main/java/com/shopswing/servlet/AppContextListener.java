package com.shopswing.servlet;

import com.shopswing.utils.DBConnection;
import com.shopswing.utils.InitDB;
import com.shopswing.utils.PopulateDB;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("ShopSwing Application is starting up...");
        System.out.println("Database JDBC URL (masked): " + DBConnection.getJdbcUrlForLogs());
        System.out.println("Database file / host info: " + DBConnection.getSqliteFilePath());
        try {
            System.out.println("Initializing database tables...");
            InitDB.initTables();
            
            System.out.println("Populating database with default catalog data...");
            PopulateDB.main(new String[]{});
            
            System.out.println("Database setup complete!");
        } catch (Exception e) {
            System.err.println("Failed to initialize database on startup!");
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("ShopSwing Application is shutting down...");
    }
}
