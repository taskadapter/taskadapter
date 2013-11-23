package com.taskadapter.webui;

/**
 * Basic session data holder. Holds all and only data relevant to one web-user.
 */
public final class WebUserSession {
    /** User web page container. */
    public final PageContainer pageContainer;

    /** Tracker for this session. */
    public final Tracker tracker;

    /** Creates a new web session. */
    public WebUserSession(PageContainer pageContainer, Tracker tracker) {
        this.pageContainer = pageContainer;
        this.tracker = tracker;
    }
}
