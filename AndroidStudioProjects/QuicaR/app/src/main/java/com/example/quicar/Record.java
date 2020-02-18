package com.example.quicar;



public class Record {
    private Request request;
    private Long dateTime;
    private Float payment;
    private Float rating;

    public Record(Request request, Float payment, Float rating) {
        this.request = request;
        this.payment = payment;
        this.rating = rating;
        this.dateTime = System.currentTimeMillis();
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public Float getReqPayment() {
        return payment;
    }

    public void setReqPayment(Float payment) {
        this.payment = payment;
    }

    public Float getReqRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }
}
