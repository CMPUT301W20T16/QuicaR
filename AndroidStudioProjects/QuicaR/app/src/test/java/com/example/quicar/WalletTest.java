package com.example.quicar;


import com.example.user.BankAccount;
import com.example.user.Wallet;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WalletTest {
    private Wallet mockWallet() {
        Wallet wallet = new Wallet();
        return wallet;
    }

    /**
     * Test for getter, however if setter passed, which is mean Getter passed
     */
    @Test
    public void testGetter() {
        ;
        // inside Setter
    }

    /**
     * Test for License Setter and Getter
     */
    @Test
    public void testAddBackAccount() {
//        Wallet wallet = mockWallet();
//        BankAccount bankAccount = new BankAccount();
//        ArrayList<BankAccount> bankAccountArrayList = new ArrayList<BankAccount>();
//        bankAccountArrayList.add(bankAccount);
//        wallet.setBankAccountArrayList(bankAccountArrayList);
//        assertEquals(wallet.getBankAccountArrayList(), bankAccountArrayList);

    }

    /**
     * Test for License Setter and Getter
     */
    @Test
    public void testPayPassword() {
        Wallet wallet = mockWallet();
        wallet.setPayPassword("123456");
        assertEquals(wallet.getPayPassword(),"123456");

    }

    /**
     * Test for License Setter and Getter
     */
    @Test
    public void testBalance() {
        Wallet wallet = mockWallet();
        wallet.setBalance(Float.valueOf("123.2"));
        assertEquals(wallet.getBalance(),Float.valueOf("123.2"));
    }


    /**
     * Test for License Setter and Getter
     */
    @Test
    public void testSetOpen() {
        Wallet wallet = mockWallet();
        wallet.setOpen(true);
        assertTrue(wallet.isOpen());
    }


}
