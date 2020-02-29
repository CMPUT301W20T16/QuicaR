package com.example.quicar;

import com.google.firebase.firestore.PropertyName;

import java.util.Date;

public class BankAccount {

    // no more accNo
    @PropertyName("cardNo")
    private String cardnumber;

    @PropertyName("expireDate")
    private Date expireDate;

    @PropertyName("ccvCode")
    private String ccvCode;

    public BankAccount() {
        this.cardnumber = "";
        this.expireDate = new Date();
        this.ccvCode = "";
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public void setCcvCode(String ccvCode) {
        this.ccvCode = ccvCode;
    }

    public void setCardnumber(String cardnumber) {
        this.cardnumber = cardnumber;
    }

    public String getCardnumber() {
        return cardnumber;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public String getCcvCode() {
        return ccvCode;
    }
}