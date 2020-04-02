package com.example.quicar;


import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;

import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

public class GoogleMapTest {


    @Test
    public void testGetDirections1() throws Exception{

            DirectionsResult result =
                    DirectionsApi.getDirections(getGeoContext(), "Sydney, AU", "Melbourne, AU").await();
            assertNotNull(result);
            assertNotNull(result.toString(), "result.toString() succeeded");
            assertNotNull(result.geocodedWaypoints);
            assertNotNull(Arrays.toString(result.geocodedWaypoints));
            assertEquals(2, result.geocodedWaypoints.length);
            assertEquals("ChIJP3Sa8ziYEmsRUKgyFmh9AQM", result.geocodedWaypoints[0].placeId);
            assertEquals("ChIJ90260rVG1moRkM2MIXVWBAQ", result.geocodedWaypoints[1].placeId);
            assertNotNull(result.routes);
            assertNotNull(Arrays.toString(result.routes));
            assertEquals(1, result.routes.length);
            assertNotNull(result.routes[0]);
            assertEquals("M31 and National Highway M31", result.routes[0].summary);
            assertEquals(1, result.routes[0].legs.length);
            assertEquals("Melbourne VIC, Australia", result.routes[0].legs[0].endAddress);
            assertEquals("Sydney NSW, Australia", result.routes[0].legs[0].startAddress);


    }





    protected GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        geoApiContext.setQueryRateLimit(3)
                .setApiKey("AIzaSyCyECZAmZ2NxQz10Qijm-ngagqBdHJblzk")
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
        return geoApiContext;
    }








}
