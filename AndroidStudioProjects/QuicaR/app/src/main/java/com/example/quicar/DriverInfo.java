package com.example.quicar;

import java.util.Date;
import com.google.firebase.firestore.PropertyName;

public class DriverInfo {

    @PropertyName("rating")
    private Double rating = 0.;

    @PropertyName("order number")
    private Integer orderNumber = 0;

    @PropertyName("plate number")
    private String plateNumber;

    @PropertyName("license")
    private String license;

    @PropertyName("sin no")
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

    //    protected void setRatingAndOrder(Double newRating){
//        this.rating = (this.rating * this.orderNumber + newRating) / (++this.orderNumber);
//    }
//
//    protected Double getRating(){
//        return this.rating;
//    }
//
//    protected Integer getOrderNumber(){
//        return this.orderNumber;
//    }
//
//    protected void setPlateNumber(String info){
//        this.plateNumber = info;
//    }
//
//    protected String getPlateNumber(){
//        return this.plateNumber;
//    }
//
//    protected void setLisence(String license){
//        this.license = license;
//    }
//
//    protected String getLisence(){
//        return this.license;
//    }
//
//    protected void setSinNumber(String info){
//        this.sinNumber = info;
//    }
//
//    protected String getSinNumber(){
//        return this.sinNumber;
//    }

}