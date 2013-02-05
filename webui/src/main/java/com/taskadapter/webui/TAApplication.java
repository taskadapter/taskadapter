package com.taskadapter.webui;

import com.taskadapter.auth.AuthException;
import com.taskadapter.auth.BasicCredentialsManager;
import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.auth.cred.CredentialsStore;
import com.taskadapter.auth.cred.FSCredentialStore;
import com.taskadapter.webui.service.EditableCurrentUserInfo;
import com.taskadapter.webui.service.EditorManager;
import com.taskadapter.webui.service.Services;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Vaadin web application entry point.
 */
@Theme("mytheme")
public class TAApplication extends UI {
    public static final String ADMIN_LOGIN_NAME = "admin";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TAApplication.class);
    
    private final Services services;
    
    private final Authenticator authenticator;
    
    private final CredentialsManager credentialsManager;
    
    public TAApplication(File dataRootFolder) {
        services = new Services(dataRootFolder,
                EditorManager.fromResource("editors.txt"));

        final CredentialsStore credStore = new FSCredentialStore(
                services.getFileManager());
        credentialsManager = new BasicCredentialsManager(
                credStore, 50);
        authenticator = new Authenticator(credentialsManager,
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

    /**
     * The public no-arg constructor is required for Vaadin.
     */
    @SuppressWarnings("unused")
    public TAApplication() {
        this(getDefaultRootFolder());
    }

    @Override
    public void init(VaadinRequest request) {
        authenticator.authenticate();

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setWidth(100, Unit.PERCENTAGE);
        setContent(layout);

        
        Navigator navigator = new Navigator(layout, services,
                credentialsManager, authenticator);
        navigator.show(new ConfigsPage());
    }

    /**
     * @return user.home / taskadapter
     */
    private static File getDefaultRootFolder() {
        String userHome = System.getProperty("user.home");
        return new File(userHome, "taskadapter");
    }
}
