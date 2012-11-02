package com.taskadapter.webui;

import com.taskadapter.web.data.Messages;
import com.taskadapter.web.service.Services;
import com.vaadin.ui.Component;

public abstract class Page {
    private static final String BUNDLE_NAME = "com.taskadapter.webui.data.messages";
    // TODO !! do not create instances in every single page!
    protected static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    abstract public String getPageGoogleAnalyticsID();

    abstract public Component getUI();

    protected Navigator navigator;
    protected Services services;

    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }

    public void setServices(Services services) {
        this.services = services;
    }
}
