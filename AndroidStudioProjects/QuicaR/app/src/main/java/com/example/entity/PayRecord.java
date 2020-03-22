package com.example.entity;

import com.example.user.User;

import java.util.Date;


/**
 * This is the class that store completed payment record with user get money, user pay money, payment and date
 */
public class PayRecord {

    private User toUser;
    private User fromUser;
    private Float payment;
    private Date dateTime;

    /**
     * This is an empty constructor (needed for storing into firebase directly)
     */
    public PayRecord(){}

    /**
     * This is the constructor that takes in initial values to create an instance
     * @param toUser
     *  user who get the money
     * @param fromUser
     *  user who pay the money
     * @param payment
     *  the amount of the payment
     */
    public PayRecord(User toUser, User fromUser, Float payment){
        this.toUser = toUser;
        this.fromUser = fromUser;
        this.payment = payment;
        this.dateTime = new Date();
    }

    /**
     * This method return the user who get the money
     * @return
     *  toUser of the payment record
     */
    public User getToUser() {
        return toUser;
    }

    /**
     * This method set the toUser of the payment record
     * @param toUser
     *  the user who get the money to be set into the payment record
     */
    public void setToUser(User toUser) {
        this.toUser = toUser;
        // might want to test if the user is valid(incomplete)
    }

    /**
     * This method return the user who pay the money
     * @return
     *  fromUser of the payment record
     */
    public User getFromUser() {
        return fromUser;
    }

    /**
     * This method set the fromUser of the payment record
     * @param fromUser
     *  the user who pay the money to be set into the payment record
     */
    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
        // might want to test if the user is valid
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
     *  Date and time in Date (data type)
     */
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * This method return the payment of the payment record
     * @return
     *  Amount of the payment
     */

    public Float getPayment() {
        return payment;
    }

    /**
     * This method set the amount of payment record
     * @param payment
     *  Amount of the payment
     */

    public void setPayment(Float payment) {
        this.payment = payment;
    }

}
