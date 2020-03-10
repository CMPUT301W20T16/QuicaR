package com.example.quicar;

import java.io.Serializable;

public class Location implements Serializable {
    Double lat;
    Double lon;

    public Location() {
        lat = null;
        lon = null;
    }

    public Location(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}
