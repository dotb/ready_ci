package com.squarepolka.readyci.util.time;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TimeUtilsTest {

    public TimeUtils subject;

    @Before
    public void setUp() {
        subject = new TimeUtils();
    }

    @Test
    public void getFormattedTaskTime() {
        String result = subject.getFormattedTaskTime(2000);
        assertEquals("Formatted time is correct", "0 days 0 hours 0 minutes 2 seconds 0 milliseconds", result);
    }

    @Test
    public void convertMillisecondsToReadableTime() {
        TimeUnit result = subject.convertMillisecondsToReadableTime(2000, 1000, TimeUtils.TIME_SECOND_ONE);
        assertEquals("Calculated time for seconds is correct", 1, result.getCalculatedTime());
    }
}
