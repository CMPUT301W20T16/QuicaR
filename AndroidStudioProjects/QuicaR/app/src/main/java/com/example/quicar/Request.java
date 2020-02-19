package com.example.quicar;


public class Request {
    private Location start;
    private Location destination;
    private User rider;
    private User driver;

    public Request() {}

    public Request(Location start, Location destination, User rider, User driver) {
        this.start = start;
        this.destination = destination;
        this.rider = rider;
        this.driver = driver;
    }

    public Location getStart() {
        return start;
    }

    public void setStart(Location start) {
        this.start = start;
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    public User getRider() {
        return rider;
    }

    public void setRider(User rider) {
        this.rider = rider;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }
}
