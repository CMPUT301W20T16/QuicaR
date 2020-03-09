package com.example.quicar;


import java.io.Serializable;
import java.util.Date;

import com.google.firebase.firestore.PropertyName;

public class User implements Serializable {

    private AccountInfo accountInfo;

    private boolean isDriver = false;

    protected User() {
        this.accountInfo = new AccountInfo();
        this.isDriver = false;
    }

    @PropertyName("account")
    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    @PropertyName("account")
    public void setAccountInfo(AccountInfo accountinfo) {
        this.accountInfo = accountinfo;
    }

    @PropertyName("isDriver")
    public boolean isDriver() {
        return isDriver;
    }

    @PropertyName("isDriver")
    public void setDriver(boolean driver) {
        isDriver = driver;
    }
//    protected void setIsDriver(){
//        this.isDriver = (this.accountinfo.getDriverInfo() != null);
//    }
//
//    protected boolean getIsDriver(){
//        return this.isDriver;
//    }
//
    protected void setAccountInfo(String accNo, String firstname, String lastname, Date birthDate, String gender, String phone, String email, String username, String password, Wallet wallet){
        this.accountInfo.setAccNo(accNo);
        this.accountInfo.setFirstName(firstname);
        this.accountInfo.setLastName(lastname);
        this.accountInfo.setBirthDate(birthDate);
        this.accountInfo.setGender(gender);
        this.accountInfo.setUserName(username);
        this.accountInfo.setPassword(password);
        this.accountInfo.setPhone(phone);
        this.accountInfo.setEmail(email);
        this.accountInfo.setWallet(wallet);
    }

//    protected Accountinfo getAccountinfo(){
//        return this.accountinfo;
//    }

    protected void setName(String name) {
        setAccountInfo(null, null, null, null, null, null, null, name, null, null);
    }

    protected String getName() {
        return this.accountInfo.getUserName();
    }

    protected void setBasic(String email, String password) {
        setAccountInfo(null, null, null, null, null, null, email, null, password, null);
    }

}