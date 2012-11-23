package com.taskadapter.webui;

import com.taskadapter.web.service.Services;
import com.vaadin.ui.Component;

public abstract class Page {
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
