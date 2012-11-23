package com.taskadapter.webui;

import com.taskadapter.auth.BasicCredentialsManager;
import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.auth.cred.CredentialsStore;
import com.taskadapter.auth.cred.FSCredentialStore;
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

    private final Services services;
    
    public TAApplication(File dataRootFolder) {
        services = new Services(dataRootFolder,
                EditorManager.fromResource("editors.txt"));
        services.getLicenseManager().loadInstalledTaskAdapterLicense();
        services.getUserManager().createFirstAdminUserIfNeeded();
        
    }
    
    public TAApplication() {
        this(getDefaultRootFolder());
    }

    @Override
    public String getVersion() {
        return services.getCurrentTaskAdapterVersion();
    }

    @Override
    public void init() {
        setTheme("mytheme");

        VerticalLayout layout = new VerticalLayout();
        layout.setWidth(100, Sizeable.UNITS_PERCENTAGE);
        mainWindow.setContent(layout);
        setMainWindow(mainWindow);

        final CredentialsStore credStore = new FSCredentialStore(
                services.getFileManager());
        final CredentialsManager credentialsManager = new BasicCredentialsManager(
                credStore, 50);
        
        Navigator navigator = new Navigator(layout, services, credentialsManager);
        navigator.show(new ConfigsPage());
    }

    @Override
    public void onRequestStart(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        services.getCookiesManager().init(httpServletRequest, httpServletResponse);
    }

    @Override
    public void onRequestEnd(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
    }

    /**
     * @return user.home / taskadapter
     */
    private static File getDefaultRootFolder() {
        String userHome = System.getProperty("user.home");
        return new File(userHome, "taskadapter");
    }
}
