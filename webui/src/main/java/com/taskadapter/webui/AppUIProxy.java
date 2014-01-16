package com.taskadapter.webui;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

/**
 * Proxy to an actual UI factory. Just because vaadin does not provide a good
 * way to set such factory for the servlet.
 * 
 */
public final class AppUIProxy extends UIProvider {

    /** Proxied instance. */
    static UIProvider instance;
    
    @Override
    public UI createInstance(UICreateEvent event) {
        return instance.createInstance(event);
    }

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        return instance.getUIClass(event);
    }
}
