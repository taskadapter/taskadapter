package com.taskadapter.web.event;

public interface Tracker {
    void trackEvent(EventCategory category, String action, String label);

    void trackEvent(EventCategory category, String action, String label, int value);
}
