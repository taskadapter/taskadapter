package com.taskadapter.webui;

import java.io.File;

import org.vaadin.googleanalytics.tracking.GoogleAnalyticsTracker;

import com.taskadapter.auth.BasicCredentialsManager;
import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.auth.cred.CredentialsStore;
import com.taskadapter.auth.cred.FSCredentialStore;
import com.taskadapter.webui.service.EditorManager;
import com.taskadapter.webui.service.Preservices;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

/**
 * Provider of the Task Adapter application.
 */
final class TAApplicationProvider extends UIProvider {

    private static final String GOOGLE_ANALYTICS_ID = "UA-3768502-12";

    /**
     * Application operation root.
     */
    private final File rootFolder;

    /**
     * Global credentials manager. TODO: It is not threadsafe yet, but should
     * be.
     */
    private final CredentialsManager credentialsManager;

    /**
     * Global services.
     */
    private final Preservices services;

    /**
     * Creates a new task application provider. Initializes all core services
     * (like credentials manager, etc...).
     */
    public TAApplicationProvider() {
        rootFolder = getDefaultRootFolder();

        final CredentialsStore credentialsStore = new FSCredentialStore(
                rootFolder);
        credentialsManager = new BasicCredentialsManager(credentialsStore, 50);

        services = new Preservices(rootFolder,
                EditorManager.fromResource("editors.txt"), credentialsManager);

        // FIXME: WTF?
        services.licenseManager.loadInstalledTaskAdapterLicense();
    }

    @Override
    public UI createInstance(UICreateEvent event) {
        final DummyUI ui = new DummyUI();
        final GoogleAnalyticsTracker gaTracker = new GoogleAnalyticsTracker(
                GOOGLE_ANALYTICS_ID, "none");
        gaTracker.extend(ui);

        SessionController.manageSession(services, credentialsManager,
                new WebUserSession(ui, new GATrackerImpl(gaTracker)));

        return ui;
    }

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        return DummyUI.class;
    }

    /**
     * Calculates a defaut taskadapter root folder. Current implementation
     * returns <code>user.home / taskadapter</code>.
     * 
     * @return task adapter root folder.
     */
    private static File getDefaultRootFolder() {
        final String userHome = System.getProperty("user.home");
        return new File(userHome, "taskadapter");
    }

}
