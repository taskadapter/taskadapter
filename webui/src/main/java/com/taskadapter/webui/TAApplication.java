package com.taskadapter.webui;

import com.vaadin.Application;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;


/**
 * This is the web application entry point.
 *
 * @author Alexey Skorokhodov
 */
public class TAApplication extends Application {

    private final Window mainWindow = new Window("Task Adapter");

    private final Services services = new Services();

    private Navigator navigator;

    @Override
    public String getVersion() {
        return services.getUpdateManager().getCurrentVersion();
    }

    @Override
    public void init() {
        setTheme("mytheme");

        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");
        navigator = new Navigator(layout, services);

        mainWindow.setContent(layout);
        setMainWindow(mainWindow);

        navigator.show(Navigator.HOME);
    }
}
