package com.example.quicar;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DriverInfoTest {
    private DriverInfo mockDriver() {
        DriverInfo driverInfo = new DriverInfo();
        return driverInfo;
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
    public void testLicense() {
        DriverInfo driverInfo = mockDriver();
        driverInfo.setLicense("1234");
        assertEquals(driverInfo.getLicense(), "1234");

    }

    /**
     * Test for rating Setter and Getter
     */
    @Test
    public void testRating() {
        DriverInfo driverInfo = mockDriver();
        driverInfo.setRating(Double.valueOf("1.2"));
        assertEquals(driverInfo.getRating(), Double.valueOf("1.2"));
    }

    /**
     * Test for  plate number Setter and Getter
     */
    @Test
    public void testPlateNumber() {
        DriverInfo driverInfo = mockDriver();
        driverInfo.setPlateNumber("12345");
        assertEquals(driverInfo.getPlateNumber(), "12345");
    }

    /**
     * Test for sin number Setter and Getter
     */
    @Test
    public void testSinNumber() {
        DriverInfo driverInfo = mockDriver();
        driverInfo.setSinNumber("12345");
        assertEquals(driverInfo.getSinNumber(), "12345");
    }

    /**
     * Test for order number Setter and Getter
     */
    @Test
    public void testOrderNumber() {
        DriverInfo driverInfo = mockDriver();
        driverInfo.setOrderNumber(Integer.parseInt("123"));
        assertEquals(driverInfo.getOrderNumber(), Integer.parseInt("123"));
    }


}
