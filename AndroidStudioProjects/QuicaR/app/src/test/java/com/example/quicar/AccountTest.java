package com.example.quicar;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountTest {
    private AccountInfo mockAccount() {
        AccountInfo accountInfo = new AccountInfo();
        return accountInfo;
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
     * Test for Accno Setter and Getter
     */
    @Test
    public void testAccountNumber() {
        AccountInfo accountInfo = mockAccount();
        accountInfo.setAccNo("1234");
        assertEquals(accountInfo.getAccNo(), "1234");

    }

    /**
     * Test for DriverInfo Setter and Getter
     */
    @Test
    public void testSetDriverInfo() {
        AccountInfo accountInfo = mockAccount();
        accountInfo.setDriverInfo(Double.valueOf("1.2"),"12345","12345","12345");
        assertEquals(accountInfo.getDriverInfo().getRating().toString(), "1.2");
        assertEquals(accountInfo.getDriverInfo().getPlateNumber(), "12345");
        assertEquals(accountInfo.getDriverInfo().getLicense(), "12345");
        assertEquals(accountInfo.getDriverInfo().getSinNumber(), "12345");


    }
    /**
     * Test for wallet Setter and Getter
     */
    @Test
    public void testWalletSetterGetter() {
        Wallet wallet =  new Wallet();
        final float balance = Float.parseFloat("100");
        wallet.setBalance(balance);
        AccountInfo accountInfo = mockAccount();
        accountInfo.setWallet(wallet);

        assertEquals(wallet.getBalance(), balance);

    }
    /**
     * Test for Email Setter and Getter
     */
    @Test
    public void testEmail() {
        AccountInfo accountInfo = mockAccount();
        accountInfo.setEmail("123@gmail.com");
        assertEquals(accountInfo.getEmail(), "123@gmail.com");

    }

    /**
     * Test for phone Setter and Getter
     */
    @Test
    public void testPhone() {
        AccountInfo accountInfo = mockAccount();
        accountInfo.setPhone("1234567");
        assertEquals(accountInfo.getPhone(), "1234567");

    }
    /**
     * Test for userName Setter and Getter
     */
    @Test
    public void testUserName() {
        AccountInfo accountInfo = mockAccount();
        accountInfo.setUserName("1234");
        assertEquals(accountInfo.getUserName(), "1234");

    }

    /**
     * Test for FirstName Setter and Getter
     */
    @Test
    public void testFirstName() {
        AccountInfo accountInfo = mockAccount();
        accountInfo.setFirstName("Teemo");
        assertEquals(accountInfo.getFirstName(), "Teemo");

    }

    /**
     * Test for LastName Setter and Getter
     */
    @Test
    public void testLastName() {
        AccountInfo accountInfo = mockAccount();
        accountInfo.setLastName("Carry");
        assertEquals(accountInfo.getLastName(), "Carry");

    }

    /**
     * Test for Password Setter and Getter
     */
    @Test
    public void testPassword() {
        AccountInfo accountInfo = mockAccount();
        accountInfo.setPassword("winAllGame");
        assertEquals(accountInfo.getPassword(), "winAllGame");

    }

    /**
     * Test for BirthDate Setter and Getter
     */
    @Test
    public void testBirthDate() {
        AccountInfo accountInfo = mockAccount();
        String sBirthDate =  "1234-11-02";
        Date birthDate;

        try {
            birthDate = (Date) new SimpleDateFormat("yyyy-MM-dd").parse(sBirthDate);
        } catch (
                ParseException e) {
            e.printStackTrace();
            birthDate = null;
        }
        accountInfo.setBirthDate(birthDate);
        assertEquals(accountInfo.getBirthDate(), birthDate);

    }




    /**
     * Test for Gender Setter and Getter
     */
    @Test
    public void testGender() {
        AccountInfo accountInfo = mockAccount();
        accountInfo.setGender("male");
        assertEquals(accountInfo.getGender(), "male");

    }






}
