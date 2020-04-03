package com.example.quicar;

import com.example.user.BankAccount;
import com.example.user.User;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BankAccountTest {

    private BankAccount mockBankAccount() {
        BankAccount bankAccount = new BankAccount();
        return  bankAccount;
    }


    @Test
    public void testSetAndGetNameOnCard() {
        BankAccount bankAccount  = mockBankAccount();
        bankAccount.setNameOnCard("Mush");
        assertEquals("Mush", bankAccount.getNameOnCard());
    }

    @Test
    public void testSetAndGetsetExpireDate() {
//        BankAccount bankAccount  = mockBankAccount();
//        bankAccount.setExpireDate((Date) new SimpleDateFormat("MM/yy/2020").parse("04032020"));
//        assertEquals("04/03/2020", bankAccount.getExpireDate());
    }

    @Test
    public void testSetAndGetCardnumber(){
        BankAccount bankAccount  = mockBankAccount();
        bankAccount.setCardnumber("1234567891234567");
        assertEquals("1234567891234567", bankAccount.getCardnumber());
    }

    @Test
    public void testGetLastFour(){
        BankAccount bankAccount  = mockBankAccount();
        bankAccount.setCardnumber("1234567891234567");
        assertEquals("4 5 6 7", bankAccount.getLastFour());
    }

    @Test
    public void testSetAndGetCcvCode(){
        BankAccount bankAccount  = mockBankAccount();
        bankAccount.setCcvCode("301");
        assertEquals("301", bankAccount.getCcvCode());
    }

    @Test
    public void testSetAndGetType(){
        ;
    }

}
