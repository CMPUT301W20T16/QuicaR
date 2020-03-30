package com.example.entity;

import com.example.user.User;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;


/**
 * This is the class that store start location, destination, rider, driver, status of reequest and
 * the estimated cost of the request / order.
 */
public class Request implements Serializable {

    private String rid;
    private Location start;
    private String startAddrName;
    private Location destination;
    private String destAddrName;
    private User rider;
    private User driver;
    private Boolean isAccepted;
    private Boolean isPickedUp;
    private Boolean hasArrived;
    private Float estimatedCost;

    /**
     * This is an empty constructor (needed for storing into firebase directly)
     */
    public Request() {}

    /**
     * This is the constructor that takes in initial values to create an instance
     * @param start
     *  location of starting point
     * @param destination
     *  location of destination
     * @param rider
     *  rider of the request / order
     * @param driver
     *  driver of the request / order
     * @param estimatedCost
     *  estimated cost of the request / order
     */
    public Request(Location start, String startAddrName, Location destination, String destAddrName,
                   User rider, User driver, Float estimatedCost) {
        this.start = start;
        this.startAddrName = startAddrName;
        this.destination = destination;
        this.destAddrName = destAddrName;
        this.rider = rider;
        this.driver = driver;
        this.isAccepted = false;
        this.isPickedUp = false;
        this.hasArrived = false;
        this.estimatedCost = estimatedCost;
    }

    /**
     * This method return the id of the request
     * This id is assigned using Firebase
     * @return
     */
    @PropertyName("requestID")
    public String getRid() {
        return rid;
    }

    /**
     * This method set the value of request's id
     * This id is obtained from Firebase so that it is guarantee to be unique
     * @param rid
     *  id of the request
     */
    @PropertyName("requestID")
    public void setRid(String rid) {
        this.rid = rid;
    }

    /**
     * This method return the starting location
     * @return
     *  location of starting point
     */
    public Location getStart() {
        return start;
    }

    /**
     * This method set the starting location
     * @param start
     *  location of starting point
     */
    public void setStart(Location start) {
        this.start = start;
    }

    /**
     * This method return the name of start address
     * @return
     *  name of start address
     */
    @PropertyName("startingAddress")
    public String getStartAddrName() {
        return startAddrName;
    }

    /**
     * This method set the value of the name of start address
     * @param startAddrName
     *  candidate start address name
     */
    @PropertyName("startingAddress")
    public void setStartAddrName(String startAddrName) {
        this.startAddrName = startAddrName;
    }

    /**
     * This method return the name of destination address
     * @return
     *  name of destination address
     */
    @PropertyName("destinationAddress")
    public String getDestAddrName() {
        return destAddrName;
    }

    /**
     * This method set the value of the name of destination
     * @param destAddrName
     *  candidate destination address name
     */
    @PropertyName("destinationAddress")
    public void setDestAddrName(String destAddrName) {
        this.destAddrName = destAddrName;
    }

    /**
     * This method retunr the destination
     * @return
     *  destination
     */
    public Location getDestination() {
        return destination;
    }

    /**
     * This method set the destination
     * @param destination
     *  destination
     */
    public void setDestination(Location destination) {
        this.destination = destination;
    }

    /**
     * This method return the rider
     * @return
     *  rider
     */
    public User getRider() {
        return rider;
    }

    /**
     * This method set the rider
     * @param rider
     *  rider
     */
    public void setRider(User rider) {
        this.rider = rider;
    }

    /**
     * This method return the driver
     * @return
     *  driver
     */
    public User getDriver() {
        return driver;
    }

    /**
     * This method set the driver
     * @param driver
     *  driver
     */
    public void setDriver(User driver) {
        this.driver = driver;
    }

    /**
     * This method return the accepted status of the request
     * @return
     *  status of the request (True or False)
     */
    @PropertyName("isAccepted")
    public Boolean getAccepted() {
        return isAccepted;
    }

    /**
     * This method set the accepted status of the request
     * @param accepted
     *  accepted status of the request
     */
    @PropertyName("isAccepted")
    public void setAccepted(Boolean accepted) {
        isAccepted = accepted;
    }

    /**
     * This method get the picked up status of request
     * @return
     *  picked up status of the request
     */
    public Boolean getPickedUp() {
        return isPickedUp;
    }

    /**
     * This method set the picked up status of request
     * @param pickedUp
     *  picked up status of request
     */
    public void setPickedUp(Boolean pickedUp) {
        isPickedUp = pickedUp;
    }

    /**
     * This method return the arriving state of the request
     * @return
     *  arriving state of the request
     */
    public Boolean getHasArrived() {
        return hasArrived;
    }

    /**
     * This method set the arriving state of the request
     * @param hasArrived
     *  arriving state of the request
     */
    public void setHasArrived(Boolean hasArrived) {
        this.hasArrived = hasArrived;
    }

    /**
     * This method return the estimated cost
     * @return
     *  estimated cost
     */
    public float getEstimatedCost() {
        return estimatedCost;
    }

    /**
     * This method set the estimated cost
     * @param estimatedCost
     *  estimated cost
     */
    public void setEstimatedCost(float estimatedCost) {
        this.estimatedCost = estimatedCost;
    }
}