package com.example.quicar;

import java.util.ArrayList;
import com.google.firebase.firestore.PropertyName;
import java.util.Date;

public class Wallet {

    private Float balance;
    private ArrayList<BankAccount> bankAccountArrayList;


// no more these 3 attributes
//    private String cardnumber;
//    private Date expireDate;
//    private String ccvCode;


    public Wallet() {
        this.balance = 0.f;
        this.bankAccountArrayList = new ArrayList<>();
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

    public void setBalance(Float balance) {
        this.balance = balance;
    }



//    public void setQrGenrator(QR qrGenrator) {
//        this.qrGenrator = qrGenrator;
//    }

//    public ArrayList<BankAccount> getBankAccountarrayList() {
//        return bankAccountArrayList;
//    }


    @PropertyName("bankAccountList")
    public ArrayList<BankAccount> getBankAccountArrayList() {
        return bankAccountArrayList;
    }

    @PropertyName("bankAccountList")
    public void setBankAccountArrayList(ArrayList<BankAccount> bankAccountArrayList) {
        this.bankAccountArrayList = bankAccountArrayList;
    }

    public Float getBalance() {
        return balance;
    }

//    public QR getQrGenrator() {
//        return qrGenrator;
//    }

}