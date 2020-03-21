package com.example.user;

import java.io.Serializable;
import java.util.Date;

public class AccountInfo implements Serializable {
    /**
    This java class Account
     */

    private DriverInfo driverInfo;
    private Wallet wallet;
    private String email;
    private String phone;
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String gender;
    private String accNo;

    public AccountInfo() {
        this.driverInfo = new DriverInfo();
        this.wallet = new Wallet();
        this.password = null;
        this.accNo = null;
        this.phone = null;
        this.email = null;
        this.userName = null;
        this.firstName = null;
        this.lastName = null;
        this.birthDate = new Date();
        this.gender = null;
    }


    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public void setDriverInfo(Double rating, String plateNumber, String license, String sinNumber) {
        this.driverInfo.setLicense(license);
        this.driverInfo.setPlateNumber(plateNumber);
        this.driverInfo.setSinNumber(sinNumber);
        this.driverInfo.setRating(rating);
    }

    public DriverInfo getDriverInfo() {
        return driverInfo;
    }

    public void setDriverInfo(DriverInfo driverInfo) {
        this.driverInfo = driverInfo;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAccNo() {
        return accNo;
    }
}