package com.nepseinsider.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Reusable database configuration class.
 * Provides a single point of entry for JDBC connections.
 *
 * Update USERNAME and PASSWORD to match your local MySQL/XAMPP setup.
 */
public class DBConfig {

    private static final String URL      = "jdbc:mysql://localhost:3306/nepseinsider_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = ""; // XAMPP default is empty

    static {
        try {
            // Load the MySQL JDBC driver once at class loading
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found on classpath", e);
        }
    }

    /**
     * Returns a new database connection.
     * Always use try-with-resources to ensure connections are closed.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * Safely closes JDBC resources. Null-safe and exception-safe.
     */
    public static void close(Connection con, Statement stmt, ResultSet rs) {
        try { if (rs   != null) rs.close();   } catch (SQLException ignored) {}
        try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        try { if (con  != null) con.close();  } catch (SQLException ignored) {}
    }
}
