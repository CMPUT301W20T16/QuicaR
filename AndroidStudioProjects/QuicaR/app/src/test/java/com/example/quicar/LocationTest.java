package com.example.quicar;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LocationTest {

    private Location mockLocation() {
        return new Location(10.0d, 20d);
    }

    @Test
    public void testGetter() {
        Location location = mockLocation();
        assertEquals(10d, (double)location.getLat());
        assertEquals(20d, (double)location.getLon());
    }

    @Test
    public void testSetter() {
        Location location = new Location();
        // test set latitude
        assertEquals(0d, (double)location.getLat());
        location.setLat(110d);
        assertEquals(110d, (double)location.getLat());

        // test set longitude
        assertEquals(0d, (double)location.getLon());
        location.setLon(110d);
        assertEquals(110d, (double)location.getLon());
    }
}
