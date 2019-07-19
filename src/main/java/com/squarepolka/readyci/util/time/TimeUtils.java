package com.squarepolka.readyci.util.time;


public class TimeUtils {
    public static final long TIME_MILLISECOND_ONE = 1;
    public static final long TIME_SECOND_ONE = 1000;
    public static final long TIME_MINUTE_ONE = TIME_SECOND_ONE * 60;
    public static final long TIME_HOUR_ONE = TIME_MINUTE_ONE * 60;
    public static final long TIME_DAY_ONE = TIME_HOUR_ONE * 24;

    public String getFormattedTaskTime(long timeDifference) {

        TimeUnit days = convertMillisecondsToReadableTime(timeDifference, timeDifference, TIME_DAY_ONE);
        TimeUnit hours = convertMillisecondsToReadableTime(timeDifference, days.getRemainingTime(), TIME_HOUR_ONE);
        TimeUnit minutes = convertMillisecondsToReadableTime(timeDifference, hours.getRemainingTime(), TIME_MINUTE_ONE);
        TimeUnit seconds = convertMillisecondsToReadableTime(timeDifference, minutes.getRemainingTime(), TIME_SECOND_ONE);
        TimeUnit milliseconds = convertMillisecondsToReadableTime(timeDifference, seconds.getRemainingTime(), TIME_MILLISECOND_ONE);

        return String.format("%s days %s hours %s minutes %s seconds %s milliseconds", days, hours, minutes, seconds, milliseconds);
    }

    public TimeUnit convertMillisecondsToReadableTime(long totalTime, long timeRemaining, long timeUnit) {
        long calculatedTime = timeRemaining / timeUnit;
        long newRemainingTime = totalTime - (calculatedTime * timeUnit);
        TimeUnit readableTime = new TimeUnit(newRemainingTime, calculatedTime);
        return readableTime;
    }

}
