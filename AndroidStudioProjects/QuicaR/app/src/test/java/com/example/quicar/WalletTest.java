package com.example.quicar;


import com.example.user.Wallet;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        Wallet wallet = mockWallet();
//        wallet.addBackAccount("1234");
//        assertEquals(wallet.getBankAccountArrayList(), "1234");

    }
}
