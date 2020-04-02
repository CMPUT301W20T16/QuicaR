package com.example.user;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;

public class DriverInfo implements Serializable {

    private Double rating = 5.0;
    private Integer orderNumber = 0;
    private String plateNumber;
    private String license;
    private String sinNumber;

    public DriverInfo(){
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double newRating) {
        this.rating = newRating;
//        this.rating = (this.rating * this.orderNumber + newRating) / (++this.orderNumber);
    }
    public void autoCmputAndSetRate(Double newRating) {
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