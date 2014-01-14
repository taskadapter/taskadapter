package com.taskadapter.webui;

import com.taskadapter.auth.BasicCredentialsManager;
import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.auth.cred.CredentialsStore;
import com.taskadapter.auth.cred.FSCredentialStore;
import com.taskadapter.license.LicenseManager;
import com.taskadapter.webui.service.EditableCurrentUserInfo;
import com.taskadapter.webui.service.Services;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertEquals;

public class NavigatorTest {
    private Services services;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void beforeEachTest() {
        services = TestServicesFactory.createServices(tempFolder.getRoot());
    }

    @Test
    public void feedbackPageIsShownWithoutLogin() {
        Navigator navigator = getNavigator();
        navigator.show(new SupportPage("1.1", new LicenseManager(tempFolder.getRoot())));
        assertEquals("support", navigator.getCurrentPage().getPageGoogleAnalyticsID());
    }

    @Test
    public void homePageRedirectsToLoginIfNotLoggedIn() {
        Navigator navigator = getNavigator();
        navigator.show(new ConfigsPage());
        assertEquals("login", navigator.getCurrentPage().getPageGoogleAnalyticsID());
    }

    private Navigator getNavigator() {
        Window window = new Window("Task Adapter");
        VerticalLayout layout = new VerticalLayout();
        window.setContent(layout);
        final CredentialsStore cs = new FSCredentialStore(services.getFileManager());
        final CredentialsManager cm = new BasicCredentialsManager(cs, 50);
        return new Navigator(layout, services, cm, new Authenticator(cm, new EditableCurrentUserInfo()));
    }
}
