package com.example.quicar;

import java.util.Date;

public class BankAccount {

    // no more accNo
    private String cardnumber;
    private Date expireDate;
    private String ccvCode;

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
