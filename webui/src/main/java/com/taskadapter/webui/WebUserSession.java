package com.taskadapter.webui;

import com.taskadapter.web.uiapi.ConfigId;

/**
 * Basic session data holder. Holds all and only data relevant to one web-user.
 */
public final class WebUserSession {
    /** User web page container. */
    public final PageContainer pageContainer;

    /** Tracker for this session. */
    public final Tracker tracker;

    private ConfigId currentConfigId = null;

    /** Creates a new web session. */
    public WebUserSession(PageContainer pageContainer, Tracker tracker) {
        this.pageContainer = pageContainer;
        this.tracker = tracker;
    }

    public ConfigId getCurrentConfigId() {
        return currentConfigId;
    }

    public boolean hasCurrentConfig() {
        return currentConfigId != null;
    }

    public void setCurrentConfigId(ConfigId currentConfigId) {
        this.currentConfigId = currentConfigId;
    }

    public void clearCurrentConfig() {
        this.currentConfigId = null;
    }

}
