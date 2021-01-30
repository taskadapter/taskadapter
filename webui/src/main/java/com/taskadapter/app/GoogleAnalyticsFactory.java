package com.taskadapter.app;

import com.taskadapter.webui.GATrackerImpl;

public class GoogleAnalyticsFactory {
    public static final String GOOGLE_ANALYTICS_ID = "UA-3768502-12";

    public static GATrackerImpl create() {
        return new GATrackerImpl();
    }
}
