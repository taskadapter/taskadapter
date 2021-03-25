package com.taskadapter.web.event;

public class SchedulerStatusChanged implements Event {
    private final boolean schedulerEnabled;

    public SchedulerStatusChanged(boolean schedulerEnabled) {
        this.schedulerEnabled = schedulerEnabled;
    }

    public boolean isSchedulerEnabled() {
        return schedulerEnabled;
    }
}