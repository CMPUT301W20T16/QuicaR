package com.example.user;

import com.google.firebase.database.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

    private AccountInfo accountInfo;

    private boolean isDriver = false;

    public User() {
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

    public void setAccountInfo(String accNo, String firstname, String lastname, Date birthDate, String gender, String phone, String email, String username, String password, Wallet wallet){
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


    public void setDriverInfo(Double rating, String plateNumber, String license, String sinNumber){
        this.accountInfo.setDriverInfo( rating,  plateNumber,  license,  sinNumber);
    }

    @Exclude
    public void setName(String name) {
        setAccountInfo(null, null, null, null, null, null, null, name, null, new Wallet());
    }

    @Exclude
    public String getName() {
        return this.accountInfo.getUserName();
    }

    public void setBasic(String username, String email, String password) {
        setAccountInfo(null, null, null, null, null, null, email, username, password, new Wallet());
    }

}
