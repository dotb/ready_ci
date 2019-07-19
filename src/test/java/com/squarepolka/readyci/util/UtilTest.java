package com.squarepolka.readyci.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class UtilTest {

    @InjectMocks
    private Util subject;

    @Test
    public void arrayToString() {
        List<String> arrayString = new ArrayList<>();
        arrayString.add("1");
        arrayString.add("2");
        arrayString.add("3");
        String result = Util.arrayToString(arrayString);
        assertEquals("String array is correct", "1 2 3", result);
    }
}