package com.napier.sem;

/**
 * CountryReport data class representing a country report entry.
 * Contains information about a country including code, name, continent, region, population, and capital.
 */
public class CountryReport {
    private String code;
    private String name;
    private String continent;
    private String region;
    private long population;
    private String capital;

    /**
     * Constructor for CountryReport.
     *
     * @param code Country code (e.g., "USA", "GBR")
     * @param name Country name
     * @param continent Continent name
     * @param region Region name
     * @param population Total population
     * @param capital Capital city name
     */
    public CountryReport(String code, String name, String continent, String region, long population, String capital) {
        this.code = code;
        this.name = name;
        this.continent = continent;
        this.region = region;
        this.population = population;
        this.capital = capital;
    }

    // Getters
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getContinent() {
        return continent;
    }

    public String getRegion() {
        return region;
    }

    public long getPopulation() {
        return population;
    }

    public String getCapital() {
        return capital;
    }

    // Setters (optional, for flexibility)
    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setPopulation(long population) {
        this.population = population;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    /**
     * Returns a string representation of the CountryReport.
     *
     * @return Formatted string with country information
     */
    @Override
    public String toString() {
        return String.format("CountryReport{code='%s', name='%s', continent='%s', region='%s', population=%d, capital='%s'}",
                code, name, continent, region, population, capital);
    }
}
