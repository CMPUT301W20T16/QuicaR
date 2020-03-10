package com.example.quicar;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RecordTest {
    private Record mockNullRecord() {
        return new Record();
    }

    @Test
    public void testGetter() {
        final Record record = mockNullRecord();

        assertThrows(IllegalArgumentException.class, () -> {
            record.getDateTimeString();
        });
        assertNull(record.getDateTime());
        assertNull(record.getReqPayment());
        assertNull(record.getRequest());
        assertNull(record.getReqRating());
    }

    @Test
    public void testSetter() {

        Record record = mockNullRecord();

        Date newDate = new Date();
        record.setDateTime(newDate);
        assertEquals(newDate, record.getDateTime());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm", Locale.CANADA);
        String dateString = sdf.format(newDate);
        assertEquals(dateString, record.getDateTimeString());

        record.setRating(5.f);
        assertEquals(5.f, (float)record.getReqRating());

        record.setReqPayment(888.f);
        assertEquals(888f, (float)record.getReqPayment());

        Request request = new Request();
        record.setRequest(request);
        assertEquals(request, record.getRequest());

    }
}
