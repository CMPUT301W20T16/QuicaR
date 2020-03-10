package com.example.quicar;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RequestTest {

    private Request mockNullRequest() {
        return new Request();
    }

    //new Location(), new Location(), new User(), new User(), 10.f

    @Test
    public void testGetter() {
        Request request = mockNullRequest();

        assertNull(request.getAccepted());
        assertNull(request.getPickedUp());
        assertNull(request.getDestination());
        assertNull(request.getStart());
        assertNull(request.getDriver());
        assertNull(request.getRider());
        assertNull(request.getEstimatedCost());
    }

    @Test
    public void testSetter() {
        Request request = mockNullRequest();

        request.setEstimatedCost(40f);
        assertEquals(40f, (float)request.getEstimatedCost());

        request.setAccepted(Boolean.TRUE);
        assertTrue(request.getAccepted());

        request.setPickedUp(Boolean.TRUE);
        assertTrue(request.getPickedUp());

        request.setDestination(new Location(123d, 123d));
        assertEquals(123d, (double)request.getDestination().getLat());
        assertEquals(123d, (double)request.getDestination().getLon());

        request.setStart(new Location(123d, 123d));
        assertEquals(123d, (double)request.getStart().getLat());
        assertEquals(123d, (double)request.getStart().getLon());

        User newUser = new User();
        request.setRider(newUser);
        assertEquals(newUser, request.getRider());

        request.setDriver(newUser);
        assertEquals(newUser, request.getDriver());

    }
}
