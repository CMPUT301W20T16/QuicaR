package com.example.quicar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.google.firebase.firestore.PropertyName;

public class BankAccount implements Serializable {

    // no more accNo
    @PropertyName("name")
    private String nameOnCard;

    @PropertyName("cardNo")
    private String cardnumber;

    @PropertyName("expireDate")
    private Date expireDate;

    @PropertyName("ccvCode")
    private String ccvCode;

    @PropertyName("type")
    private String type;

    public BankAccount() {
        this.nameOnCard = "";
        this.cardnumber = "";
        this.expireDate = new Date();
        this.ccvCode = "";
        this.type = "";
    }

    public void setNameOnCard(String name) {
        this.nameOnCard = name;
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

    public void setCardType() {
        String[] cardTypeList = new String[]{"Debit Card", "MasterCard"};
        Random r = new Random(System.currentTimeMillis());
        int index = r.nextInt(2);
        this.type = cardTypeList[index];
    }

    public String getNameOnCard() {
        return this.cardnumber;
    }

    public String getCardnumber() {
        return this.cardnumber;
    }

    public String getLastFour(){return this.cardnumber.substring(12, 13) + " "+ this.cardnumber.substring(13, 14) + " " + this.cardnumber.substring(14, 15) + " " + this.cardnumber.substring(15, 16);}

    public Date getExpireDate() {
        return this.expireDate;
    }

    public String getCcvCode() {
        return this.ccvCode;
    }

    public String getType() {
        return this.type;
    }
}
