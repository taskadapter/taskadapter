package com.taskadapter.webui;

import com.taskadapter.web.service.EditorManager;
import com.taskadapter.web.service.Services;
import com.vaadin.Application;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * Vaadin web application entry point.
 */
public class TAApplication extends Application implements HttpServletRequestListener {

    private final Window mainWindow = new Window("Task Adapter");

    private Services services;
    private File dataRootFolder;

    @Override
    public String getVersion() {
        return getServices().getCurrentTaskAdapterVersion();
    }

    @Override
    public void init() {
        setTheme("mytheme");

        VerticalLayout layout = new VerticalLayout();
        layout.setWidth(100, Sizeable.UNITS_PERCENTAGE);
        mainWindow.setContent(layout);
        setMainWindow(mainWindow);

        getServices().getAuthenticator().init();

        Navigator navigator = new Navigator(layout, services);
        navigator.show(new ConfigsPage());
    }

    @Override
    public void onRequestStart(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        getServices().getCookiesManager().init(httpServletRequest, httpServletResponse);
    }

    @Override
    public void onRequestEnd(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
    }

    /**
     * Used for testing.
     */
    void setDataRootFolder(File dataRootFolder) {
        this.dataRootFolder = dataRootFolder;
    }

    Services getServices() {
        if (services == null) {
            services = new Services(getDataRootFolder(), EditorManager.fromResource("editors.txt"));
            services.getLicenseManager().loadInstalledTaskAdapterLicense();
            services.getUserManager().createFirstAdminUserIfNeeded();
        }
        return services;
    }

    private File getDataRootFolder() {
        return dataRootFolder == null ? getDefaultRootFolder() : dataRootFolder;
    }

    /**
     * @return user.home / taskadapter
     */
    private File getDefaultRootFolder() {
        String userHome = System.getProperty("user.home");
        return new File(userHome, "taskadapter");
    }
}
