package com.taskadapter.app;

import com.taskadapter.webui.GATrackerImpl;

public class GoogleAnalyticsFactory {
    /**
     * Google Analytics app ID to submit web app usage against.
     * <p>
     * it is okay to leave this ID in the code. the worst case is that a hacker will plug this ID into some other
     * (unrelated) application or a webpage, which will push unrelated web traffic to this google analytics report.
     * this data is just a helper to see what pages or app features are used by the app users,
     * to help drive future app development.
     */
    public static final String GOOGLE_ANALYTICS_ID = "UA-3768502-12";

    public static GATrackerImpl create() {
        return new GATrackerImpl();
    }
}
