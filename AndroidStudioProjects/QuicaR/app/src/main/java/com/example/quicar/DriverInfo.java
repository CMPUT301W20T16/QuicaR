package com.example.quicar;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;

public class DriverInfo implements Serializable {

    private Double rating = 0.;
    private Integer orderNumber = 0;
    private String plateNumber;
    private String license;
    private String sinNumber;

    protected DriverInfo(){
//        this.rating = 0.0;
//        this.orderNumber = 0;
//        this.plateNumber = null;
//        this.license = null;
//        this.sinNumber = null;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double newRating) {
        //this.rating = rating;
        this.rating = (this.rating * this.orderNumber + newRating) / (++this.orderNumber);
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getSinNumber() {
        return sinNumber;
    }

    public void setSinNumber(String sinNumber) {
        this.sinNumber = sinNumber;
    }

}