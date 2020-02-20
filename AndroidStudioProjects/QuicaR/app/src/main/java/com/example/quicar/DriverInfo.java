package com.example.quicar;

import java.util.Date;

public class DriverInfo {
    private Double rating;
    private Integer orderNumber;
    private String plateNumber;
    private String license;
    private String sinNumber;

    protected DriverInfo(){
        this.rating = 0.0;
        this.orderNumber = 0;
        this.plateNumber = null;
        this.license = null;
        this.sinNumber = null;
    }

    protected void setRatingAndOrder(Double newRating){
        this.rating = (this.rating * this.orderNumber + newRating) / (++this.orderNumber);
    }

    protected Double getRating(){
        return this.rating;
    }

    protected Integer getOrderNumber(){
        return this.orderNumber;
    }

    protected void setPlateNumber(String info){
        this.plateNumber = info;
    }

    protected String getPlateNumber(){
        return this.plateNumber;
    }

    protected void setLisence(String license){
        this.license = license;
    }

    protected String getLisence(){
        return this.license;
    }

    protected void setSinNumber(String info){
        this.sinNumber = info;
    }

    protected String getSinNumber(){
        return this.sinNumber;
    }

}
