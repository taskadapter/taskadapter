package com.taskadapter.webui;

import com.taskadapter.web.uiapi.UISyncConfig;

/**
 * Basic session data holder. Holds all and only data relevant to one web-user.
 */
public final class WebUserSession {
    /** User web page container. */
    public final PageContainer pageContainer;

    /** Tracker for this session. */
    public final Tracker tracker;

    private UISyncConfig currentConfig = null;

    /** Creates a new web session. */
    public WebUserSession(PageContainer pageContainer, Tracker tracker) {
        this.pageContainer = pageContainer;
        this.tracker = tracker;
    }

    public UISyncConfig getCurrentConfig() {
        return currentConfig;
    }

    public void setCurrentConfig(UISyncConfig currentConfig) {
        this.currentConfig = currentConfig;
    }

    public void clearCurrentConfig() {
        this.currentConfig = null;
    }

}
