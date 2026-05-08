package com.shopswing.servlet;

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
        try {
            System.out.println("Initializing SQLite database tables...");
            InitDB.initTables();
            
            System.out.println("Populating SQLite database with default data...");
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
