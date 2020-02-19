package com.example.quicar;

import java.util.Date;

public class User {
    private AccountInfo accountinfo;
    private boolean isDriver;

    protected User(){
        this.accountinfo = null;
        this.isDriver = false;
    }

    protected void setIsDriver(){
        this.isDriver = (this.accountinfo.getDriverInfo() != null);
    }

    protected boolean getIsDriver(){
        return this.isDriver;
    }

    protected void setAccountinfo(String accNo, String firstname, String lastname, Date birthDate, String gender, String phone, String email, String username, String password, Wallet wallet){
        this.accountinfo.setAccNo(accNo);
        this.accountinfo.setFirstname(firstname);
        this.accountinfo.setLastname(lastname);
        this.accountinfo.setBirthDate(birthDate);
        this.accountinfo.setGender(gender);
        this.accountinfo.setUsername(username);
        this.accountinfo.setPassword(password);
        this.accountinfo.setPhone(phone);
        this.accountinfo.setEmail(email);
        this.accountinfo.setWallet(wallet);
    }

    protected AccountInfo getAccountinfo(){
        return this.accountinfo;
    }

}
