package com.example.quicar;

import java.util.Date;

public class AccountInfo {
    /**
     This java class Account
     */

    private DriverInfo driverInfo;
    private Wallet wallet;
    private String email;
    private String phone;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private Date birthDate;
    private String gender;
    private String accNo;

    public AccountInfo() {
        this.driverInfo = null;
        this.wallet = null;
        this.password = null;
        this.accNo = null;
        this.phone = null;
        this.email = null;
        this.username = null;
        this.firstname = null;
        this.lastname = null;
        this.birthDate = null;
        this.gender = null;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public void setDriverInfo(Double rating, String plateNumber, String license, String sinNumber) {
        this.driverInfo.setLisence(license);
        this.driverInfo.setPlateNumber(plateNumber);
        this.driverInfo.setSinNumber(sinNumber);
        this.driverInfo.setRatingAndOrder(rating);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstname(String firstname){
        this.firstname = firstname;
    }

    public void setLastname(String lastname){
        this.lastname = lastname;
    }

    protected void setBirthDate(Date date){
        this.birthDate = date;
    }

    protected void setGender(String gender){
        this.gender = gender;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public DriverInfo getDriverInfo() {
        return this.driverInfo;
    }

    public String getAccNo() {
        return this.accNo;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getFirstname(){
        return this.firstname;
    }

    public String setLastname(){
        return this.lastname;
    }

    protected Date getBirthDate(){
        return this.birthDate;
    }

    protected String setGender(){
        return this.gender;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getEmail() {
        return this.email;
    }

    public Wallet getWallet() {
        return this.wallet;
    }
}
