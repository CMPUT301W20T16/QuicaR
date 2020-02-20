package com.example.quicar;

import java.security.PrivateKey;
import java.util.Date;

public class AccountInfo {
    /**
     This java class Account
     */

    private DriverInfo driverInfo;
    private Wallet wallet;
    private String phone;
    private String password;
    private String accNo;

    public AccountInfo() {
        this.driverInfo = null;
        this.wallet = null;
        this.password = null;
        this.accNo = null;
        this.phone = null;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public void setDriverInfo(Double rating, String plateNumber, String license, Date birthDate, String sinNumber) {
        this.driverInfo.setBirthDate(birthDate);
        this.driverInfo.setLisence(license);
        this.driverInfo.setPlateNumber(plateNumber);
        this.driverInfo.setSinNumber(sinNumber);
        this.driverInfo.setRatingAndOrder(rating);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public DriverInfo getDriverInfo() {
        return driverInfo;
    }

    public String getAccNo() {
        return accNo;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public Wallet getWallet() {
        return wallet;
    }
}