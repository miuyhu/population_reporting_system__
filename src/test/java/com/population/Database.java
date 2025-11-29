package com.population;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection utility class.
 * Handles connecting and disconnecting from the MySQL world database.
 * 
 * This class provides a simple interface for database connectivity,
 * primarily used for integration testing.
 * 
 * Note: For production use, consider using environment variables or
 * configuration files for database credentials instead of hardcoding.
 * 
 * @author Israel Ayemo
 * @author Population Reporting System Team
 */
public class Database {

    // Store a single shared connection
    private static Connection connection;

    // Default database configuration
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DATABASE = "world";
    private static final String DEFAULT_USERNAME = "root";
    private static final String DEFAULT_PASSWORD = "root"; // Changed to match App.java default

    /**
     * Connects to the MySQL database using default settings.
     *
     * @return an active database connection, or null if connection fails
     */
    public static Connection connect() {
        return connect(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_DATABASE, DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    /**
     * Connects to the MySQL database with custom settings.
     *
     * @param host Database host address
     * @param port Database port
     * @param database Database name
     * @param username Database username
     * @param password Database password
     * @return an active database connection, or null if connection fails
     */
    public static Connection connect(String host, String port, String database, String username, String password) {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Build connection URL
            String url = String.format("jdbc:mysql://%s:%s/%s?allowPublicKeyRetrieval=true&useSSL=false",
                    host, port, database);

            // Create connection
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("✓ Connected to database: " + database + " at " + host + ":" + port);
        } catch (ClassNotFoundException e) {
            System.err.println("✗ JDBC Driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("✗ Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Disconnects from the database.
     * Closes the connection if it exists and is open.
     */
    public static void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
                System.out.println("✓ Disconnected from database.");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Checks if the database connection is active.
     *
     * @return true if connected, false otherwise
     */
    public static boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Gets the current database connection.
     *
     * @return the current connection, or null if not connected
     */
    public static Connection getConnection() {
        return connection;
    }
}

