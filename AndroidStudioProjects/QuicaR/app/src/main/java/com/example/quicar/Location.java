package com.example.quicar;

public class Location {
    Float lat;
    Float lon;

    public Location() {
        lat = new Float(0);
        lon = new Float(0);
    }

    public Location(float lat, float lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }
}
