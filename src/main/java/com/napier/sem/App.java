package com.napier.sem;

import java.sql.*;

public class App {
    /**
     * Connection to MySQL database. MUST be public for Unit Testing via Mockito.
     */
    public Connection con = null;

    /**
     * Connect to the MySQL database.
     * @param location The database location (e.g., "db:3306" for Docker)
     * @param delay Delay in milliseconds before attempting connection
     */
    public void connect(String location, int delay) {
        try {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                // Wait a bit for db to start
                Thread.sleep(delay);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://" + location + "/world?allowPublicKeyRetrieval=true&useSSL=false", "root", "root");
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " + i);
                System.out.println(sqle.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect() {
        if (con != null) {
            try {
                con.close();
            } catch (Exception e) {
                System.out.println("Error closing connection to database");
            }
        }
    }

    // ----------------------------------------------------------------------
    // CORE REPORT METHODS (Generic Stubs for Testing ALL Permutations)
    // ----------------------------------------------------------------------

    /**
     * REQUIREMENT 1: All the countries in the world organised by largest population to smallest.
     * This method can also filter by Continent or Region.
     */
    public void reportCountries(String areaType, String name) {
        String whereClause = "";
        String title = "All Countries in the World";
        
        if (!areaType.equals("World") && !name.isEmpty()) {
            whereClause = "WHERE " + areaType + " = '" + name + "'";
            title = "All Countries in " + name;
        }

        try {
            Statement stmt = con.createStatement();
            String strSelect =
                    "SELECT country.Code, country.Name, country.Continent, country.Region, country.Population, " +
                            "COALESCE(city.Name, 'N/A') AS CapitalName " +
                            "FROM country " +
                            "LEFT JOIN city ON country.Capital = city.ID " +
                            whereClause +
                            " ORDER BY country.Population DESC";

            ResultSet rset = stmt.executeQuery(strSelect);

            System.out.println("\n--------------------------------------------------------------------------------");
            System.out.println(title.toUpperCase());
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("%-5s %-45s %-15s %-30s %-15s %-30s%n", 
                    "Code", "Name", "Continent", "Region", "Population", "Capital");
            System.out.println("--------------------------------------------------------------------------------");

            int count = 0;
            while (rset.next()) {
                System.out.printf("%-5s %-45s %-15s %-30s %-15d %-30s%n",
                        rset.getString("Code"),
                        rset.getString("Name"),
                        rset.getString("Continent"),
                        rset.getString("Region"),
                        rset.getLong("Population"),
                        rset.getString("CapitalName"));
                count++;
            }
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("Total countries: " + count);
            System.out.println("--------------------------------------------------------------------------------\n");

        } catch (Exception e) {
            System.out.println("Failed to get report: " + title);
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * REQUIREMENT 2: All the cities in the world organised by largest population to smallest.
     * This method can also filter by Continent, Region, Country, or District.
     */
    public void reportCities(String areaType, String name) {
        String title = "All Cities in the World";
        String sql = "";

        try {
            Statement stmt = con.createStatement();
            
            if (areaType.equals("World")) {
                sql = "SELECT city.Name, country.Name AS CountryName, city.District, city.Population " +
                      "FROM city " +
                      "JOIN country ON city.CountryCode = country.Code " +
                      "ORDER BY city.Population DESC";
            } else if (areaType.equals("Continent")) {
                sql = "SELECT city.Name, country.Name AS CountryName, city.District, city.Population " +
                      "FROM city " +
                      "JOIN country ON city.CountryCode = country.Code " +
                      "WHERE country.Continent = '" + name + "' " +
                      "ORDER BY city.Population DESC";
                title = "All Cities in " + name + " (Continent)";
            } else if (areaType.equals("Region")) {
                sql = "SELECT city.Name, country.Name AS CountryName, city.District, city.Population " +
                      "FROM city " +
                      "JOIN country ON city.CountryCode = country.Code " +
                      "WHERE country.Region = '" + name + "' " +
                      "ORDER BY city.Population DESC";
                title = "All Cities in " + name + " (Region)";
            } else if (areaType.equals("Country")) {
                sql = "SELECT city.Name, country.Name AS CountryName, city.District, city.Population " +
                      "FROM city " +
                      "JOIN country ON city.CountryCode = country.Code " +
                      "WHERE country.Name = '" + name + "' " +
                      "ORDER BY city.Population DESC";
                title = "All Cities in " + name + " (Country)";
            } else if (areaType.equals("District")) {
                sql = "SELECT city.Name, country.Name AS CountryName, city.District, city.Population " +
                      "FROM city " +
                      "JOIN country ON city.CountryCode = country.Code " +
                      "WHERE city.District = '" + name + "' " +
                      "ORDER BY city.Population DESC";
                title = "All Cities in " + name + " (District)";
            }

            ResultSet rset = stmt.executeQuery(sql);

            System.out.println("\n--------------------------------------------------------------------------------");
            System.out.println(title.toUpperCase());
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("%-40s %-40s %-30s %-15s%n", 
                    "Name", "Country", "District", "Population");
            System.out.println("--------------------------------------------------------------------------------");

            int count = 0;
            while (rset.next()) {
                System.out.printf("%-40s %-40s %-30s %-15d%n",
                        rset.getString("Name"),
                        rset.getString("CountryName"),
                        rset.getString("District"),
                        rset.getLong("Population"));
                count++;
            }
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("Total cities: " + count);
            System.out.println("--------------------------------------------------------------------------------\n");

        } catch (Exception e) {
            System.out.println("Failed to get report: " + title);
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * REQUIREMENT 3: All the capital cities in the world organised by largest population to smallest.
     * This method can also filter by Continent or Region.
     */
    public void reportCapitalCities(String areaType, String name) {
        String title = "All Capital Cities in the World";
        String sql = "";

        try {
            Statement stmt = con.createStatement();
            
            if (areaType.equals("World")) {
                sql = "SELECT city.Name, country.Name AS CountryName, city.Population " +
                      "FROM city " +
                      "JOIN country ON city.ID = country.Capital " +
                      "ORDER BY city.Population DESC";
            } else if (areaType.equals("Continent")) {
                sql = "SELECT city.Name, country.Name AS CountryName, city.Population " +
                      "FROM city " +
                      "JOIN country ON city.ID = country.Capital " +
                      "WHERE country.Continent = '" + name + "' " +
                      "ORDER BY city.Population DESC";
                title = "All Capital Cities in " + name + " (Continent)";
            } else if (areaType.equals("Region")) {
                sql = "SELECT city.Name, country.Name AS CountryName, city.Population " +
                      "FROM city " +
                      "JOIN country ON city.ID = country.Capital " +
                      "WHERE country.Region = '" + name + "' " +
                      "ORDER BY city.Population DESC";
                title = "All Capital Cities in " + name + " (Region)";
            }

            ResultSet rset = stmt.executeQuery(sql);

            System.out.println("\n--------------------------------------------------------------------------------");
            System.out.println(title.toUpperCase());
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("%-40s %-40s %-15s%n", 
                    "Name", "Country", "Population");
            System.out.println("--------------------------------------------------------------------------------");

            int count = 0;
            while (rset.next()) {
                System.out.printf("%-40s %-40s %-15d%n",
                        rset.getString("Name"),
                        rset.getString("CountryName"),
                        rset.getLong("Population"));
                count++;
            }
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("Total capital cities: " + count);
            System.out.println("--------------------------------------------------------------------------------\n");

        } catch (Exception e) {
            System.out.println("Failed to get report: " + title);
            System.out.println("Error: " + e.getMessage());
        }
    }


    // ----------------------------------------------------------------------
    // COMPLEX ASSESSMENT REPORTS (Logic Implemented)
    // ----------------------------------------------------------------------

    /**
     * REQUIREMENT 7: Language statistics showing the number of people speaking Chinese, English, Hindi, Spanish, and Arabic (with percentage of world population).
     * Shows total speakers and percentage of the world population.
     */
    public void reportLanguageStatistics() {
        try {
            // 1. Get Total World Population
            long worldPop = 0;
            Statement stmt1 = con.createStatement();
            ResultSet rset1 = stmt1.executeQuery("SELECT SUM(Population) FROM country");
            if (rset1.next()) {
                worldPop = rset1.getLong(1);
            }

            // 2. Define the languages we need
            String[] languages = {"Chinese", "English", "Hindi", "Spanish", "Arabic"};

            System.out.println("\n--------------------------------------------------------------------------------");
            System.out.println("LANGUAGE STATISTICS REPORT");
            System.out.printf("%-20s %-20s %-20s%n", "Language", "Total Speakers", "% of World Pop");
            System.out.println("--------------------------------------------------------------------------------");

            for (String lang : languages) {
                // Logic: Sum of (Country Population * Language Percentage)
                String sql = "SELECT SUM(c.Population * (cl.Percentage / 100)) " +
                        "FROM countrylanguage cl " +
                        "JOIN country c ON cl.CountryCode = c.Code " +
                        "WHERE cl.Language = '" + lang + "'";

                Statement stmt2 = con.createStatement();
                ResultSet rset2 = stmt2.executeQuery(sql);

                if (rset2.next()) {
                    long speakers = rset2.getLong(1);
                    double percent = (worldPop > 0) ? ((double)speakers / worldPop) * 100 : 0;
                    System.out.printf("%-20s %-20d %-20.2f%%%n", lang, speakers, percent);
                }
            }
            System.out.println("--------------------------------------------------------------------------------\n");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get language report.");
        }
    }

    /**
     * REQUIREMENT 4/5/6: Population breakdown by continent/region/country (people living in cities vs not living in cities with percentages).
     * Shows total population, population in cities, and population not in cities with percentages.
     */
    public void reportPopulationSplit(String type, String name) {
        String title = type.toUpperCase() + ": " + name;

        String sqlTotalPop = "";
        String sqlCityPop = "";

        switch (type.toLowerCase()) {
            case "continent":
            case "region":
                sqlTotalPop = "SELECT SUM(Population) FROM country WHERE " + type + " = '" + name + "'";
                sqlCityPop = "SELECT SUM(c.Population) AS CitySum FROM city c JOIN country co ON c.CountryCode = co.Code WHERE co." + type + " = '" + name + "'";
                break;
            case "country":
                sqlTotalPop = "SELECT Population FROM country WHERE Name = '" + name + "'";
                sqlCityPop = "SELECT SUM(Population) FROM city WHERE CountryCode = (SELECT Code FROM country WHERE Name = '" + name + "')";
                break;
            default:
                System.out.println("Invalid type specified: " + type);
                return;
        }

        try (Statement stmt = con.createStatement()) {
            long totalPop = 0;
            ResultSet rsetTotal = stmt.executeQuery(sqlTotalPop);
            if (rsetTotal.next()) {
                totalPop = rsetTotal.getLong(1);
            }

            long popInCities = 0;
            ResultSet rsetCity = stmt.executeQuery(sqlCityPop);
            if (rsetCity.next()) {
                popInCities = rsetCity.getLong(1);
            }

            long popNotCities = totalPop - popInCities;

            double percentInCities = (totalPop > 0) ? ((double) popInCities / totalPop) * 100 : 0;
            double percentNotCities = (totalPop > 0) ? ((double) popNotCities / totalPop) * 100 : 0;

            System.out.println("\n--------------------------------------------------------------------------------");
            System.out.println("POPULATION BREAKDOWN: " + title);
            System.out.printf("%-20s %-20s%n", "Category", "Population");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("%-20s %-20d%n", "Total Population", totalPop);
            System.out.printf("%-20s %-20d (%.2f%%)%n", "Living in Cities", popInCities, percentInCities);
            System.out.printf("%-20s %-20d (%.2f%%)%n", "Not in Cities", popNotCities, percentNotCities);
            System.out.println("--------------------------------------------------------------------------------\n");

        } catch (SQLException e) {
            System.out.println("Database error during population split report: " + e.getMessage());
        }
    }

    /**
     * REQUIREMENT 8: Top N cities in a district organised by largest population to smallest.
     */
    public void reportTopNCitiesInDistrict(String district, int n) {
        try {
            Statement stmt = con.createStatement();
            String strSelect =
                    "SELECT city.Name, country.Name AS CountryName, city.District, city.Population " +
                            "FROM city " +
                            "JOIN country ON city.CountryCode = country.Code " +
                            "WHERE city.District = '" + district + "' " +
                            "ORDER BY city.Population DESC " +
                            "LIMIT " + n;

            ResultSet rset = stmt.executeQuery(strSelect);

            System.out.println("\n--------------------------------------------------------------------------------");
            System.out.println("TOP " + n + " CITIES IN DISTRICT: " + district.toUpperCase());
            System.out.printf("%-20s %-30s %-20s %-10s%n", "Name", "Country", "District", "Population");
            System.out.println("--------------------------------------------------------------------------------");

            while (rset.next()) {
                System.out.printf("%-20s %-30s %-20s %-10s%n",
                        rset.getString("Name"),
                        rset.getString("CountryName"),
                        rset.getString("District"),
                        rset.getInt("Population"));
            }
            System.out.println("--------------------------------------------------------------------------------\n");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get district report.");
        }
    }

    /**
     * REQUIREMENT: Accessible Information (Single Query - Example for City)
     */
    public void reportSpecificCityPopulation(String cityName) {
        try {
            Statement stmt = con.createStatement();
            String strSelect = "SELECT Population FROM city WHERE Name = '" + cityName + "'";
            ResultSet rset = stmt.executeQuery(strSelect);

            if (rset.next()) {
                System.out.println("--------------------------------------------------");
                System.out.println("POPULATION CHECK: " + cityName);
                System.out.println("Population: " + rset.getLong("Population"));
                System.out.println("--------------------------------------------------\n");
            } else {
                System.out.println("City not found: " + cityName);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * MAIN EXECUTION METHOD: Runs all required assessment reports.
     */
    public static void main(String[] args) {
        App a = new App();

        // Connect to database (Uses 'db:3306' for Docker by default)
        String dbLocation = (args.length < 1) ? "db:3306" : args[0];
        int dbDelay = (args.length < 2) ? 10000 : Integer.parseInt(args[1]);

        a.connect(dbLocation, dbDelay);

        if (a.con != null) {
            System.out.println("\n==================================================================================");
            System.out.println("                    POPULATION REPORTING SYSTEM");
            System.out.println("                    GENERATING ALL REPORTS");
            System.out.println("==================================================================================\n");

            // REQUIREMENT 1: All the countries in the world organised by largest population to smallest
            System.out.println("REQUIREMENT 1: All Countries in the World");
            a.reportCountries("World", "");

            // REQUIREMENT 2: All the cities in the world organised by largest population to smallest
            System.out.println("REQUIREMENT 2: All Cities in the World");
            a.reportCities("World", "");

            // REQUIREMENT 3: All the capital cities in the world organised by largest population to smallest
            System.out.println("REQUIREMENT 3: All Capital Cities in the World");
            a.reportCapitalCities("World", "");

            // REQUIREMENT 4: Population breakdown by Continent
            System.out.println("REQUIREMENT 4: Population Breakdown by Continent");
            a.reportPopulationSplit("Continent", "Asia");

            // REQUIREMENT 5: Population breakdown by Region
            System.out.println("REQUIREMENT 5: Population Breakdown by Region");
            a.reportPopulationSplit("Region", "Western Europe");

            // REQUIREMENT 6: Population breakdown by Country
            System.out.println("REQUIREMENT 6: Population Breakdown by Country");
            a.reportPopulationSplit("Country", "France");

            // REQUIREMENT 7: Language Statistics
            System.out.println("REQUIREMENT 7: Language Statistics");
            a.reportLanguageStatistics();

            // REQUIREMENT 8: Top N Cities in a District
            System.out.println("REQUIREMENT 8: Top N Cities in a District");
            a.reportTopNCitiesInDistrict("California", 5);
        }

        a.disconnect();
    }
}