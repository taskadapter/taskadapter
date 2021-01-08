package com.taskadapter.webui;

//import org.vaadin.googleanalytics.tracking.GoogleAnalyticsTracker;

/** Google analytics tracker implementation. */
public class GATrackerImpl implements Tracker {

//    final GoogleAnalyticsTracker tracker;

//    public GATrackerImpl(GoogleAnalyticsTracker tracker) {
//        this.tracker = tracker;
//    }

    @Override
    public void trackPage(String name) {
//        tracker.trackPageview("/" + name);
    }

    @Override
    public void trackEvent(EventCategory category, String action, String label) {
//        tracker.trackEvent(category.name(), action, label);
    }

    @Override
    public void trackEvent(EventCategory category, String action, String label, Integer value) {
//        tracker.trackEvent(category.name(), action, label, value);
    }
}
