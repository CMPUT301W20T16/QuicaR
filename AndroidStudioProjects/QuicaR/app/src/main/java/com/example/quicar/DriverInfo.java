package com.example.quicar;

import java.util.Date;

public class DriverInfo {
    private Double rating;
    private Integer orderNumber;
    private String plateNumber;
    private String license;
    private Date birthDate;
    private String sinNumber;

    protected DriverInfo(){
        this.rating = 0.0;
        this.orderNumber = 0;
        this.plateNumber = null;
        this.license = null;
        this.birthDate = null;
        this.sinNumber = null;
    }

    protected void costumerRating(Double newRating){
        this.rating = (rating * orderNumber + newRating) / (orderNumber++);
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

    protected void setBirthDate(Date date){
        this.birthDate = date;
    }

    protected Date getBirthDate(){
        return this.birthDate;
    }

    protected void setSinNumber(String info){
        this.sinNumber = info;
    }

    protected String getSinNumber(){
        return this.sinNumber;
    }

}
