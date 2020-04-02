package com.example.quicar;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.AddressType;
import com.google.maps.model.GeocodingResult;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeocodingAPiTest {
    @Test
    public void testGeocodeLibraryType() throws Exception {

            GeocodingResult[] results = GeocodingApi.newRequest(getGeoContext()).address("80 FR").await();

            assertEquals(1, results.length);
            assertEquals(3, results[0].types.length);
            assertEquals(AddressType.ESTABLISHMENT, results[0].types[0]);
            //assertEquals(AddressType., results[0].types[1]);
            assertEquals(AddressType.POINT_OF_INTEREST, results[0].types[2]);
            assertNotNull(Arrays.toString(results));

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
