package com.example.quicar;

import java.util.Date;

public class DriverInfo {
    private Double rating = 0.0;
    private Integer orderNumber = 0;
    private String plateNumber = null;
    private String license = null;
    private Date birthDate = null;
    private String sinNumber = null;

    protected void costumerRating(Double newRating){
        rating = (rating * orderNumber + newRating) / (orderNumber++);
    }

    protected Double getRating(){
        return rating;
    }

    protected Integer getOrderNumber(){
        return orderNumber;
    }

    protected void setPlateNumber(String info){
        plateNumber = info;
    }

    protected String getPlateNumber(){
        return plateNumber;
    }

    protected void setLisence(String info){
        license = info;
    }

    protected String getLisence(){
        return license;
    }

    protected void setBirthDate(Date date){
        birthDate = date;
    }

    protected Date getBirthDate(){
        return birthDate;
    }

    protected void setSinNumber(String info){
        sinNumber = info;
    }

    protected String getSinNumber(){
        return sinNumber;
    }

}
