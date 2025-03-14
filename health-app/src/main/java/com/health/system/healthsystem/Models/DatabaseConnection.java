package com.health.system.healthsystem.Models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // the database file will be created in the project root directory
    private static final String URL = "jdbc:sqlite:health_system.db";
    
    public static Connection connect() {
        Connection conn = null;
        try {
            // ensure the driver is loaded
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(URL);
            System.out.println("Database connection established");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
        return conn;
    }
}