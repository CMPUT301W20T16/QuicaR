package com.example.quicar;

/**
 * Location class that have longitude and latitude attributes
 */
public class Location {
    Double lat;
    Double lon;

    public Location() {
        lat = new Double(0);
        lon = new Double(0);
    }

    /**
     * This is the constructor that takes in latitude and longitude
     * @param lat
     *  candidate latitude
     * @param lon
     *  candidate longitude
     */
    public Location(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * This method return the latitude
     * @return
     *  latitude
     */
    public Double getLat() {
        return lat;
    }

    /**
     * This method set the value of latitude
     * @param lat
     *  candidate latitude
     */
    public void setLat(Double lat) {
        this.lat = lat;
    }

    /**
     * This method return the longitude
     * @return
     *  longitude
     */
    public Double getLon() {
        return lon;
    }

    /**
     * Thid method set the value of longitude
     * @param lon
     *  candidate longitude
     */
    public void setLon(Double lon) {
        this.lon = lon;
    }
}
