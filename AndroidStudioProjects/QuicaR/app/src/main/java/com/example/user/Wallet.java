package com.example.user;

import java.io.Serializable;
import java.util.ArrayList;

import com.example.entity.PayRecord;
import com.google.firebase.firestore.PropertyName;

public class Wallet implements Serializable {

    private Float balance;
    private ArrayList<BankAccount> bankAccountArrayList;
    private String payPassword;
    private boolean open;
//    private ArrayList<PayRecord> payRecordList;

// no more these 3 attributes
//    private String cardnumber;
//    private Date expireDate;
//    private String ccvCode;

    public Wallet() {
        this.balance = 0.f;
        this.bankAccountArrayList = new ArrayList<>();
        this.payPassword = "";
        this.open = false;
//        this.payRecordList = new ArrayList<>();
    }

    // may change it to add
//    public void setBankAccountArrayList(ArrayList<BankAccount> bankAccountarrayList) {
//        this.bankAccountArrayList = bankAccountarrayList;
//    }

    // add new bankaccount
    public void addBackAccount (BankAccount bankAccount) {
        this.bankAccountArrayList.add(bankAccount);
    }


    // remove
    public void removeBankAccount(BankAccount bankAccount) {
        int pos = this.bankAccountArrayList.indexOf(bankAccount);
        this.bankAccountArrayList.remove(pos);
    }

    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    @PropertyName("bankAccountList")
    public ArrayList<BankAccount> getBankAccountArrayList() {
        return bankAccountArrayList;
    }

    @PropertyName("bankAccountList")
    public void setBankAccountArrayList(ArrayList<BankAccount> bankAccountArrayList) {
        this.bankAccountArrayList = bankAccountArrayList;
    }

    public String getPayPassword() {
        return payPassword;
    }

    public void setPayPassword(String payPassword) {
        this.payPassword = payPassword;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = true;
    }

    //    public ArrayList<PayRecord> getPayRecordList() {
//        return payRecordList;
//    }

//    public void setPayRecordList(ArrayList<PayRecord> payRecordList) {
//        this.payRecordList = payRecordList;
//    }
}