package com.napier.sem;

/**
 * Alternative entry point for the Population Reporting System.
 * This class provides a simple way to run the application with default settings.
 * 
 * Note: The main entry point is in App.java, which provides more configuration options.
 * This Main class is kept for compatibility and simple execution.
 */
public class Main {
    /**
     * Main method - delegates to App.main() with default parameters.
     * 
     * @param args Command line arguments (optional):
     *             args[0] - Database location (default: "db:3306" for Docker, or "localhost:3306" for local)
     *             args[1] - Connection delay in milliseconds (default: 10000)
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Population Reporting System");
        System.out.println("Starting application...");
        System.out.println("========================================\n");
        
        // Delegate to App.main() which handles all the logic
        // If no args provided, App will use Docker defaults (db:3306)
        // For local testing, you can pass "localhost:3306" as first argument
        App.main(args);
    }
}
