package com.taskadapter.webui;

import com.taskadapter.web.event.EventCategory;
import com.taskadapter.web.event.Tracker;
import org.vaadin.googleanalytics.tracking.GoogleAnalyticsTracker;

import java.util.Optional;

/**
 * Google analytics tracker implementation.
 */
public class GATrackerImpl implements Tracker {

    @Override
    public void trackEvent(EventCategory category, String action, String label) {
        getTracker().ifPresent(t -> t.sendEvent(category.name(), action, label));
    }

    @Override
    public void trackEvent(EventCategory category, String action, String label, int value) {
        getTracker().ifPresent(t -> t.sendEvent(category.name(), action, label, value));
    }

    /**
     * @return empty if the tracker is not initialized yet. freaking vaadin.
     */
    private static Optional<GoogleAnalyticsTracker> getTracker() {
        return Optional.ofNullable(GoogleAnalyticsTracker.getCurrent());
    }
}
