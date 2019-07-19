package com.squarepolka.readyci.util;

public class PropertyTypeException extends RuntimeException {

    public PropertyTypeException(String propertyName) {
        super(String.format("Could not handle the value type for the property %s. Please check the type of the value and try again.", propertyName));
    }
}
