package com.taskadapter.web.event;

import com.taskadapter.web.event.Tracker;

public class NoOpGATracker implements Tracker {
    @Override
    public void trackEvent(EventCategory category, String action, String label) {
    }

    @Override
    public void trackEvent(EventCategory category, String action, String label, int value) {
    }
}
