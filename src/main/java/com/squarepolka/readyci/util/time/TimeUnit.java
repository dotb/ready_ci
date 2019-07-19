package com.squarepolka.readyci.util.time;

public class TimeUnit {
    private long remainingTime;
    private long calculatedTime;

    public TimeUnit(long remainingTime, long calculatedTime) {
        this.remainingTime = remainingTime;
        this.calculatedTime = calculatedTime;
    }

    @Override
    public String toString() {
        String timeString = Long.toString(calculatedTime);
        return timeString;
    }

    public long getRemainingTime() {
        return remainingTime;
    }

    public long getCalculatedTime() {
        return calculatedTime;
    }
}
