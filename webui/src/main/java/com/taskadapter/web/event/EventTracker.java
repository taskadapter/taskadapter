package com.taskadapter.web.event;

public class EventTracker {
    public static void trackEvent(EventCategory category, String action, String label) {
        EventBusImpl.post(new ApplicationActionEvent(category, action, label));
    }

    public static void trackEvent(EventCategory category, String action, String label, int value) {
        EventBusImpl.post(new ApplicationActionEventWithValue(category, action, label, value));
    }
}
