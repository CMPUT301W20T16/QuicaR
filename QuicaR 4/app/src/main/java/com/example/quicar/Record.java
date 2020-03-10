package com.example.quicar;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.firebase.firestore.PropertyName;

/**
 * This is the class that store completed request with date, time, payment and rating for the driver
 */
public class Record {
    @PropertyName("request")
    private Request request;

    @PropertyName("date&time")
    private Date dateTime;

    @PropertyName("payment")
    private Float payment;

    @PropertyName("rating")
    private Float rating;

    /**
     * This is an empty constructor (needed for storing into firebase directly)
     */
    public Record() {}

    /**
     * This is the constructor that takes in initial values to create an instance
     * @param request
     *  candidate request to be set
     * @param payment
     *  value of payment
     * @param rating
     *  rating of the request / order
     */
    public Record(Request request, Float payment, Float rating) {
        this.request = request;
        this.payment = payment;
        this.rating = rating;
        this.dateTime = new Date();
    }

    /**
     * This method return the request of the record
     * @return
     *  Request of the record
     */
    public Request getRequest() {
        return request;
    }

    /**
     * This method set the request of the record
     * @param request
     *  candidate request to be set into the record
     */
    public void setRequest(Request request) {
        this.request = request;
        // might want to test if the request is valid (completed)
    }

    /**
     * This method return the date and time as Date (data type)
     * @return
     *  date and time
     */
    public Date getDateTime() {
        return dateTime;
    }

    /**
     * This method set the date and time of the record
     * @param dateTime
     *  date and time in Date (data type)
     */
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * This method return the payment of the order / request
     * @return
     *  Value of payment
     */
    public Float getReqPayment() {
        return payment;
    }

    /**
     * This method set the value of payment
     * @param payment
     *  value of the payment
     */
    public void setReqPayment(Float payment) {
        this.payment = payment;
    }

    /**
     * This method return the rating of the request / order
     * @return
     *  rating of the request or order
     */
    public Float getReqRating() {
        return rating;
    }

    /**
     * This method set the rating of the request / order
     * @param rating
     *  rating of the request / order
     */
    public void setRating(Float rating) {
        this.rating = rating;
    }

    /**
     * This is an extra method that return date and time as a string in standard format
     * @return
     *  date and time as a string in standard format
     */
    public String getDateTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm", Locale.CANADA);
        if (dateTime == null)
            throw new IllegalArgumentException();
        return sdf.format(dateTime);
    }
}
