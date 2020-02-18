package com.example.quicar;

import java.util.ArrayList;
import java.util.Date;

public class Wallet {
    private QR qrGenrator;
    private Float balance;
    private ArrayList<BankAccount> bankAccountarrayList;


// no more these 3 attributes
//    private String cardnumber;
//    private Date expireDate;
//    private String ccvCode;

    // may change it to add
    public void setBankAccountarrayList(ArrayList<BankAccount> bankAccountarrayList) {
        this.bankAccountarrayList = bankAccountarrayList;
    }

    // add new bankaccount
    public void addBackAccount (BankAccount bankAccount) {
        this.bankAccountarrayList.add(bankAccount);
    }


    // remove
    public void removeBankAccount(BankAccount bankAccount) {
        int pos = this.bankAccountarrayList.indexOf(bankAccount);
        this.bankAccountarrayList.remove(pos);
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }



    public void setQrGenrator(QR qrGenrator) {
        this.qrGenrator = qrGenrator;
    }

    public ArrayList<BankAccount> getBankAccountarrayList() {
        return bankAccountarrayList;
    }

    public Float getBalance() {
        return balance;
    }

    public QR getQrGenrator() {
        return qrGenrator;
    }

}
