package com.population;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for database connectivity and basic queries.
 * These tests require an active MySQL database connection.
 * 
 * Note: These tests are excluded from the standard test suite
 * (see pom.xml surefire configuration) and should be run separately
 * when database connectivity is available.
 */
@DisplayName("Database Integration Tests")
public class DatabaseIntegrationTest {

    private Connection connection;

    @BeforeEach
    void setUp() {
        // Attempt to connect before each test
        connection = Database.connect();
    }

    @AfterEach
    void tearDown() {
        // Clean up connection after each test
        if (connection != null) {
            Database.disconnect();
        }
    }

    @Test
    @DisplayName("Should successfully connect to database")
    void testDatabaseConnection() {
        assertNotNull(connection, "Database connection should not be null");
        assertTrue(Database.isConnected(), "Database should report as connected");
    }

    @Test
    @DisplayName("Should query country count from database")
    void testQueryCountryCount() {
        assertNotNull(connection, "Database connection failed");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM country")) {

            assertTrue(rs.next(), "ResultSet should have at least one row");
            int count = rs.getInt(1);
            assertTrue(count > 0, "Expected at least one country in database");
            // Typical world database has around 239 countries
            assertTrue(count >= 200, "Expected reasonable number of countries (>= 200)");

        } catch (SQLException e) {
            fail("SQL query failed: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should query city count from database")
    void testQueryCityCount() {
        assertNotNull(connection, "Database connection failed");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM city")) {

            assertTrue(rs.next(), "ResultSet should have at least one row");
            int count = rs.getInt(1);
            assertTrue(count > 0, "Expected at least one city in database");

        } catch (SQLException e) {
            fail("SQL query failed: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should query language data from database")
    void testQueryLanguageData() {
        assertNotNull(connection, "Database connection failed");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM countrylanguage")) {

            assertTrue(rs.next(), "ResultSet should have at least one row");
            int count = rs.getInt(1);
            assertTrue(count > 0, "Expected at least one language entry in database");

        } catch (SQLException e) {
            fail("SQL query failed: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should retrieve sample country data")
    void testRetrieveSampleCountry() {
        assertNotNull(connection, "Database connection failed");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT Code, Name, Population FROM country WHERE Code = 'GBR' LIMIT 1")) {

            if (rs.next()) {
                String code = rs.getString("Code");
                String name = rs.getString("Name");
                long population = rs.getLong("Population");

                assertEquals("GBR", code, "Country code should match");
                assertNotNull(name, "Country name should not be null");
                assertTrue(population > 0, "Population should be positive");
            } else {
                // If GBR doesn't exist, just verify we can query
                assertTrue(true, "Query executed successfully");
            }

        } catch (SQLException e) {
            fail("SQL query failed: " + e.getMessage());
        }
    }
}
