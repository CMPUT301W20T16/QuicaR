package com.example.quicar;

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

    protected void setAccountinfo(Wallet wallet, String phone, String password, String accNo){
        this.accountinfo.setWallet(wallet);
        this.accountinfo.setPassword(password);
        this.accountinfo.setPhone(phone);
        this.accountinfo.setAccNo(accNo);
    }

    protected AccountInfo getAccountinfo(){
        return this.accountinfo;
    }

}
