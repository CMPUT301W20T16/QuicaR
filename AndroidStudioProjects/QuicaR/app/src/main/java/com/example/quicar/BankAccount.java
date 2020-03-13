package com.example.quicar;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

import com.google.firebase.firestore.PropertyName;

public class BankAccount implements Serializable {

    // no more accNo
    private String nameOnCard;
    private String cardNumber;
    private Date expireDate;
    private String ccvCode;
    private String type;

    public BankAccount() {
        this.nameOnCard = "";
        this.cardNumber = "";
        this.expireDate = new Date();
        this.ccvCode = "";
        this.type = "";
    }


    @PropertyName("name")
    public void setNameOnCard(String name) {
        this.nameOnCard = name;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public void setCcvCode(String ccvCode) {
        this.ccvCode = ccvCode;
    }

    @PropertyName("type")
    public void setType(String type) {
        String[] cardTypeList = new String[]{"Debit Card", "MasterCard"};
        Random r = new Random(System.currentTimeMillis());
        int index = r.nextInt(2);
        this.type = cardTypeList[index];
    }

    @PropertyName("cardNo")
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getLastFour(){
        return this.cardNumber.substring(12, 13) + " " + this.cardNumber.substring(13, 14) + " " +
                this.cardNumber.substring(14, 15) + " " + this.cardNumber.substring(15, 16);
    }

    @PropertyName("name")
    public String getNameOnCard() {
        return this.cardNumber;
    }

    @PropertyName("cardNo")
    public String getCardNumber() {
        return cardNumber;
    }

    public Date getExpireDate() {
        return this.expireDate;
    }

    public String getCcvCode() {
        return this.ccvCode;
    }

    @PropertyName("type")
    public String getType() {
        return this.type;
    }
}
