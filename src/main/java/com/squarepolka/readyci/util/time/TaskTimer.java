package com.squarepolka.readyci.util.time;

import java.util.Calendar;

public class TaskTimer {
    private Calendar taskStartTime;
    private Calendar taskEndTime;

    public static TaskTimer newStartedTimer() {
        TaskTimer taskTimer = new TaskTimer();
        taskTimer.startTiming();
        return taskTimer;
    }

    public TaskTimer() {
        taskStartTime = Calendar.getInstance();
        taskEndTime = taskStartTime;
    }

    public void startTiming() {
        taskStartTime = Calendar.getInstance();
    }

    public void stopTiming() {
        taskEndTime = Calendar.getInstance();
    }

    public String stopAndGetElapsedTime() {
        stopTiming();
        return getElapsedTime();
    }

    public String getElapsedTime() {
        TimeUtils timeUtils = new TimeUtils();
        long elapsedTime = taskEndTime.getTimeInMillis() - taskStartTime.getTimeInMillis();
        String formattedTime = timeUtils.getFormattedTaskTime(elapsedTime);
        return formattedTime;
    }

}
