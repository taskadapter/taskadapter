package com.taskadapter.webui;

import com.taskadapter.auth.AuthException;
import com.taskadapter.auth.BasicCredentialsManager;
import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.auth.cred.CredentialsStore;
import com.taskadapter.auth.cred.FSCredentialStore;
import com.taskadapter.webui.service.EditableCurrentUserInfo;
import com.taskadapter.webui.service.EditorManager;
import com.taskadapter.webui.service.Services;
import com.vaadin.Application;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Vaadin web application entry point.
 */
public class TAApplication extends Application implements HttpServletRequestListener {
    public static final String ADMIN_LOGIN_NAME = "admin";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TAApplication.class);
    
    private final Window mainWindow = new Window("Task Adapter");

    private final Services services;
    
    private final CookiesManager cookiesManager;
    
    private final Authenticator authenticator;
    
    private final CredentialsManager credentialsManager;
    
    public TAApplication(File dataRootFolder) {
        cookiesManager = new CookiesManager();
        services = new Services(dataRootFolder,
                EditorManager.fromResource("editors.txt"));

        final CredentialsStore credStore = new FSCredentialStore(
                services.getFileManager());
        credentialsManager = new BasicCredentialsManager(
                credStore, 50);
        authenticator = new Authenticator(credentialsManager,
                cookiesManager,
                (EditableCurrentUserInfo) services.getCurrentUserInfo());
        
        services.getLicenseManager().loadInstalledTaskAdapterLicense();
        
        if (!credentialsManager.doesUserExists(ADMIN_LOGIN_NAME)) {
            try {
                credentialsManager.setPrimaryAuthToken(
                        ADMIN_LOGIN_NAME, ADMIN_LOGIN_NAME);
            } catch (AuthException e) {
                LOGGER.error("Admin initialization exception", e);
            }
        }
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
        authenticator.authenticate();
        setTheme("mytheme");

        VerticalLayout layout = new VerticalLayout();
        layout.setWidth(100, Sizeable.UNITS_PERCENTAGE);
        mainWindow.setContent(layout);
        setMainWindow(mainWindow);

        
        Navigator navigator = new Navigator(layout, services,
                credentialsManager, authenticator);
        navigator.show(new ConfigsPage());
    }

    @Override
    public void onRequestStart(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        cookiesManager.init(httpServletRequest, httpServletResponse);
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
