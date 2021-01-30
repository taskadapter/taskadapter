package com.taskadapter.webui;

public class NoOpGATracker implements Tracker {
    @Override
    public void trackEvent(EventCategory category, String action, String label) {
    }

    @Override
    public void trackEvent(EventCategory category, String action, String label, Integer value) {
    }
}
