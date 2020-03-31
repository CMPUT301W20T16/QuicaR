package com.example.entity;

import java.io.Serializable;

/**
 * Location class that have longitude and latitude attributes
 */
public class Location implements Serializable {
    private Double lat;
    private Double lon;
    private String addressName;


    public Location() {

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
     *
     * @param lat
     * @param lon
     * @param addressName
     */
    public Location(Double lat, Double lon, String addressName) {
        this.lat = lat;
        this.lon = lon;
        this.addressName = addressName;
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

    /**
     * This method set the address name
     * @param addressName
     */
    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }


    /**
     * This method get the address name
     * @return
     */
    public String getAddressName() {
        return addressName;
    }

//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//
//    public String getAdminArea() {
//        return adminArea;
//    }
//
//    public void setAdminArea(String adminArea) {
//        this.adminArea = adminArea;
//    }
}
