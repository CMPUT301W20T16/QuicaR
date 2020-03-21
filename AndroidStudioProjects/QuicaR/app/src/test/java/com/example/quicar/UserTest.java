package com.example.quicar;

import com.example.user.AccountInfo;
import com.example.user.User;
import com.example.user.Wallet;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {

    private User mockUser() {
        User user = new User();
        return user;
    }

    @Test
    public void testGetter() {
        ;
        // inside Setter
    }

    @Test
    public void testSetter() {
        User user = mockUser();
        Wallet wallet = null;
        AccountInfo accountInfo;
        // test set account info
//        assertEquals(accountInfo, user.getAccountInfo());
        user.setAccountInfo("121",null,null,null,null,null,null,null,null,wallet);
        accountInfo = user.getAccountInfo();
        String accno = accountInfo.getAccNo();
        assertEquals(accno, "121");
    }

    @Test
    public void testSetDriver() {
        User user = mockUser();
        user.setDriver(true);
        assertTrue(user.isDriver());

    }

    @Test
    public void testSetDriverInfo() {
        User user = mockUser();
        user.setDriver(true);
        assertTrue(user.isDriver());
        user.setDriverInfo(Double.valueOf("1.2"),null,null,null);
        assertEquals(    Double.valueOf("1.2")    ,user.getAccountInfo().getDriverInfo().getRating());
    }



}
