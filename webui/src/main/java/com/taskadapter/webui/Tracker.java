package com.taskadapter.webui;

public interface Tracker {
    /** Tracks a page view. */
    void trackPage(String name);
    void trackEvent(String category, String action, String label);
}
